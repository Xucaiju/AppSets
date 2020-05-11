package xcj.appsets.provider

import android.app.ActivityManager
import android.content.Context
import android.content.pm.FeatureInfo
import android.content.res.Configuration
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import com.dragons.aurora.playstoreapiv2.*
import java.net.URLEncoder
import java.util.*

class NativeDeviceInfoProvider() : DeviceInfoProvider {
    private var context: Context? = null
    private var localeString: String? = null
    private var networkOperator: String = ""
    private var simOperator: String = ""
    private var gsfVersionProvider: NativeGsfVersionProvider? = null
    fun setContext(context: Context) :NativeDeviceInfoProvider{
        this.context = context
        gsfVersionProvider = NativeGsfVersionProvider(context)
        val tm: TelephonyManager? =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (null != tm) {
            networkOperator = if (null != tm.networkOperator) tm.networkOperator else ""
            simOperator = if (null != tm.simOperator) tm.simOperator else ""
        }
        return this
    }

    fun setLocaleString(localeString: String?):NativeDeviceInfoProvider {
        this.localeString = localeString
        return this
    }

    override fun getSdkVersion(): Int {
        return Build.VERSION.SDK_INT
    }

    override fun getPlayServicesVersion(): Int {
        return gsfVersionProvider!!.getGsfVersionCode(true)
    }

    override fun getMccmnc(): String {
        return simOperator
    }

    override fun getAuthUserAgentString(): String {
        return "GoogleAuth/1.4 (" + Build.DEVICE + " " + Build.ID + ")"
    }

    override fun getUserAgentString(): String {
        return ("Android-Finsky/" + URLEncoder.encode(
            gsfVersionProvider!!.getVendingVersionString(
                true
            )
        ).replace("+", "%20")
                + " ("
                + "api=3" + ","
                + "versionCode=" + gsfVersionProvider!!.getVendingVersionCode(true) + ","
                + "sdk=" + Build.VERSION.SDK_INT + ","
                + "device=" + Build.DEVICE + ","
                + "hardware=" + Build.HARDWARE + ","
                + "product=" + Build.PRODUCT + ","
                + "platformVersionRelease=" + Build.VERSION.RELEASE + ","
                + "model=" + URLEncoder.encode(Build.MODEL).replace("+", "%20")
                + "buildId=" + Build.ID + ","
                + "isWideScreen=" + (if (context!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) "1" else "0") + ","
                + "supportedAbis=" + TextUtils.join(
            ";",
            platforms
        )
                + ")")
    }

    override fun generateAndroidCheckinRequest(): AndroidCheckinRequest {
        return AndroidCheckinRequest
            .newBuilder()
            .setId(0)
            .setCheckin(checkInProto)
            .setLocale(localeString)
            .setTimeZone(TimeZone.getDefault().id)
            .setVersion(3)
            .setDeviceConfiguration(deviceConfigurationProto)
            .setFragment(0)
            .build()
    }

    private val checkInProto: AndroidCheckinProto
        private get() = AndroidCheckinProto.newBuilder()
            .setBuild(buildProto)
            .setLastCheckinMsec(0)
            .setCellOperator(networkOperator)
            .setSimOperator(simOperator)
            .setRoaming("mobile-notroaming")
            .setUserNumber(0)
            .build()

    private val buildProto: AndroidBuildProto
        private get() {
            return AndroidBuildProto.newBuilder()
                .setId(Build.FINGERPRINT)
                .setProduct(Build.HARDWARE)
                .setCarrier(Build.BRAND)
                .setRadio(Build.RADIO)
                .setBootloader(Build.BOOTLOADER)
                .setDevice(Build.DEVICE)
                .setSdkVersion(Build.VERSION.SDK_INT)
                .setModel(Build.MODEL)
                .setManufacturer(Build.MANUFACTURER)
                .setBuildProduct(Build.PRODUCT)
                .setClient("android-google")
                .setOtaInstalled(false)
                .setTimestamp(System.currentTimeMillis() / 1000)
                .setGoogleServices(playServicesVersion)
                .build()
        }

    override fun getDeviceConfigurationProto(): DeviceConfigurationProto {
        val builder: DeviceConfigurationProto.Builder = DeviceConfigurationProto.newBuilder()
        addDisplayMetrics(builder)
        addConfiguration(builder)
        return builder
            .addAllNativePlatform(platforms)
            .addAllSystemSharedLibrary(getSharedLibraries(context))
            .addAllSystemAvailableFeature(getFeatures(context))
            .addAllSystemSupportedLocale(getLocales(context))
            .setGlEsVersion((context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo.reqGlEsVersion)
            .addAllGlExtension(EglExtensionProvider.eglExtensions)
            .build()
    }

    private fun addDisplayMetrics(builder: DeviceConfigurationProto.Builder) {
        val metrics: DisplayMetrics = context!!.resources.displayMetrics
        builder
            .setScreenDensity((metrics.density * 160f).toInt())
            .setScreenWidth(metrics.widthPixels).screenHeight = metrics.heightPixels
    }

    private fun addConfiguration(builder: DeviceConfigurationProto.Builder) {
        val config: Configuration =
            context!!.resources.configuration
        builder
            .setTouchScreen(config.touchscreen)
            .setKeyboard(config.keyboard)
            .setNavigation(config.navigation)
            .setScreenLayout(config.screenLayout and 15)
            .setHasHardKeyboard(config.keyboard == Configuration.KEYBOARD_QWERTY)
            .hasFiveWayNavigation =
            config.navigation == Configuration.NAVIGATIONHIDDEN_YES
    }

    companion object {
        val platforms: List<String?>
            get() {
                return Arrays.asList(*Build.SUPPORTED_ABIS)
            }

        fun getFeatures(context: Context?): List<String> {
            val featureStringList: MutableList<String> =
                ArrayList()
            for (feature: FeatureInfo in context!!.packageManager.systemAvailableFeatures) {
                if (!TextUtils.isEmpty(feature.name)) {
                    featureStringList.add(feature.name)
                }
            }
            Collections.sort(featureStringList)
            return featureStringList
        }

        fun getLocales(context: Context?): List<String> {
            val rawLocales: MutableList<String> =
                ArrayList()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rawLocales.addAll(Arrays.asList(*context!!.assets.locales))
            } else {
                for (locale: Locale in Locale.getAvailableLocales()) {
                    rawLocales.add(locale.toString())
                }
            }
            val locales: MutableList<String> =
                ArrayList()
            for (locale: String in rawLocales) {
                if (TextUtils.isEmpty(locale)) {
                    continue
                }
                locales.add(locale.replace("-", "_"))
            }
            Collections.sort(locales)
            return locales
        }

        fun getSharedLibraries(context: Context?): List<String> {
            val libraries: List<String> =
                ArrayList(Arrays.asList(*context!!.packageManager.systemSharedLibraryNames))
            Collections.sort(libraries)
            return libraries
        }
    }
}
package xcj.appsets.livedata

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import xcj.appsets.model.ConnectionModel

class ConnectionLiveData(private var context: Context):LiveData<ConnectionModel>() {
    private var networkReceiver:BroadcastReceiver = object:BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(intent.extras != null) {
                val manager:ConnectivityManager? = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if(manager != null) {
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
                        val capabilities:NetworkCapabilities? = manager.getNetworkCapabilities(manager.activeNetwork)
                        if(capabilities != null) {
                            if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                                or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                                or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN)
                                or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                                or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                            ){
                                postValue(ConnectionModel("ONLINE", true))
                            }else{
                                postValue(ConnectionModel("OFFLINE",false))
                            }
                        }
                    }else{
                        val activeNetworkInfo = manager.activeNetworkInfo
                        try {
                            if (activeNetworkInfo != null) {
                                val isConnected = activeNetworkInfo.isConnectedOrConnecting
                                postValue(ConnectionModel(activeNetworkInfo.typeName, isConnected))
                            }
                        }catch (e:Exception){
                            postValue(ConnectionModel(activeNetworkInfo?.typeName!!,false))
                        }
                    }
                }
            }
        }
    }

    override fun onActive() {
        super.onActive()
        var nr = NetworkRequest.Builder()
        var intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkReceiver,intentFilter)
    }

    override fun onInactive() {
        context.unregisterReceiver(networkReceiver)
        super.onInactive()
    }

}
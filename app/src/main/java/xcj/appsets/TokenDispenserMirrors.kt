package xcj.appsets

import android.content.Context
import xcj.appsets.util.PreferenceUtil
import xcj.appsets.util.getCustomTokenizerURL
import xcj.appsets.util.getTokenizerURL
import xcj.appsets.util.isCustomTokenizerEnabled
import java.util.*

class TokenDispenserMirrors {
    companion object{
        private val dispenserList: MutableList<String> = ArrayList()
        @JvmStatic
        operator fun get(context: Context): String? {
            return if (isCustomTokenizerEnabled(context))
                getCustomTokenizerURL(context)
            else
                getTokenizerURL(context)
        }
        @JvmStatic
        fun setNextDispenser(context: Context, dispenserNum: Int) {
            PreferenceUtil.putString(
                context,
                Constant.PREFERENCE_TOKENIZER_URL,
                dispenserList[dispenserNum % dispenserList.size]
            )
        }
    }




    init {
        dispenserList.add("http://auroraoss.com:8080")
        dispenserList.add("http://auroraoss.in:8080")
        //dispenserList.add("http://92.42.46.11:8080");
        //dispenserList.add("https://token-dispenser.calyxinstitute.org");
    }
}
//operator
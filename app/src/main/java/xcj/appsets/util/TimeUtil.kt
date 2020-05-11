package xcj.appsets.util

import android.content.Context
import xcj.appsets.manager.LocaleManager
import java.util.*

class TimeUtil {
    companion object{
        @JvmStatic
        fun getLocalCurrentTime(context:Context){
            var canlendar = Calendar.getInstance(LocaleManager(context).locale)
            val year = canlendar[Calendar.YEAR]
            val month = canlendar[Calendar.MONTH]+1
            val day = canlendar[Calendar.DAY_OF_MONTH]
        }
    }
}
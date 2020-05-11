package xcj.appsets.database

import androidx.room.TypeConverter
import java.sql.Date

class TypeConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.let { Date.valueOf(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {

        return date?.toString()
    }


    @TypeConverter
    fun numberToDouble(number: Double?): String? {
        return number?.toString()
    }

}
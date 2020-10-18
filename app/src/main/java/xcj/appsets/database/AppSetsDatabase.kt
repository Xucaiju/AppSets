package xcj.appsets.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import xcj.appsets.database.dao.AppSetsUserFavoriteAppsDao
import xcj.appsets.database.dao.AppSetsUserReviewDao
import xcj.appsets.database.dao.TodayAppDao
import xcj.appsets.model.AppSetsTodayFavoriteApp
import xcj.appsets.model.AppSetsUserReview
import xcj.appsets.model.TodayApp

@Database(entities = [TodayApp::class, AppSetsUserReview::class, AppSetsTodayFavoriteApp::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class AppSetsDatabase : RoomDatabase() {
    abstract fun todayAppDao(): TodayAppDao
    abstract fun appSetsUserReviewDao(): AppSetsUserReviewDao
    abstract fun appSetsUserFavoriteAppsDao():AppSetsUserFavoriteAppsDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppSetsDatabase? = null

        fun getDatabase(context: Context): AppSetsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppSetsDatabase::class.java,
                    "appsets_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
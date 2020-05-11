package xcj.appsets.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import xcj.appsets.server.AppSetsServer

class SyncDataWithServerWorker(appContext:Context, params:WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        withContext(IO){
            AppSetsServer.getTodayData(applicationContext)
            AppSetsServer.getALlTodayApps(applicationContext)
            AppSetsServer.getLoggedUserFavoriteTodayApps(applicationContext)
        }
        return Result.success()
    }
}
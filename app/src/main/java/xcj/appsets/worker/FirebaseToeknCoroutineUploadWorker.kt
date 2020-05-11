package xcj.appsets.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import xcj.appsets.server.AppSetsServer

class FirebaseToeknCoroutineUploadWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        withContext(IO){
            val uploadResult = AppSetsServer.uploadFcmToken(this@FirebaseToeknCoroutineUploadWorker.applicationContext)
            if(uploadResult=="success"){
                return@withContext Result.success()
            }else{
                return@withContext Result.failure()
            }

        }

        // Do the work here--in this case, upload the images.

        //uploadImages()

        // Indicate whether the task finished successfully with the Result
        return Result.success()
    }
}
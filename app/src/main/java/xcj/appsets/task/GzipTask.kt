package xcj.appsets.task

import android.content.Context
import android.content.ContextWrapper
import org.apache.commons.io.IOUtils
import xcj.appsets.util.Log
import java.io.*
import java.util.zip.GZIPInputStream

class GZipTask(base: Context?) : ContextWrapper(base) {
    fun extract(file: File): Boolean {
        return try {
            val oldPath = file.path
            val newPath = oldPath.replace("gzip", "obb")
            val inputStream: InputStream =
                GZIPInputStream(FileInputStream(file), 131072 /*Block Size*/)
            val outputStream: OutputStream = FileOutputStream(newPath)
            IOUtils.copyLarge(inputStream, outputStream)
            file.delete()
            inputStream.close()
            outputStream.close()
            true
        } catch (e: IOException) {
            Log.e(e.message)
            false
        } catch (e: IllegalStateException) {
            Log.e(e.message)
            false
        }
    }
}
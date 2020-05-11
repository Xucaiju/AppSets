package xcj.appsets.util

object TextUtil {
    fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.isEmpty()
    }

    fun nullIfEmpty(str: String?): String? {
        return if (isEmpty(str)) null else str
    }

    fun emptyIfNull(str: String?): String {
        return str ?: ""
    }
}
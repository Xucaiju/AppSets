package xcj.appsets.task

import com.dragons.aurora.playstoreapiv2.GooglePlayAPI
import com.dragons.aurora.playstoreapiv2.SearchSuggestEntry

class SuggestionTask(private val api: GooglePlayAPI) {
    @Throws(Exception::class)
    fun getSearchSuggestions(query: String?): List<SearchSuggestEntry> {
        return api.searchSuggest(query).getEntryList()
    }

}
package xcj.appsets.model

data class CategoryModel (var categoryId:String?=null,
                          var categoryTitle:String?=null,
                          var categoryImageUrl:String?=null
){
    override fun toString(): String {
        return "CategoryModel(categoryId=$categoryId, categoryTitle=$categoryTitle, categoryImageUrl=$categoryImageUrl)"
    }
}
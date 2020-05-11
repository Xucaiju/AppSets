package xcj.appsets.model

import android.content.pm.ApplicationInfo

class ImageSource{
    private  var murl:String? = null
    private  var applicationInfo: ApplicationInfo?=null


    fun setUrl(url:String){
        this.murl= url
    }

    fun getUrl() = this.murl
    override fun toString(): String {
        return "ImageSource(murl=$murl, applicationInfo=$applicationInfo)"
    }

}
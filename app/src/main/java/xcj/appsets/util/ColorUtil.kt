package xcj.appsets.util

import android.graphics.Color

class ColorUtil {
    fun getFirstColor() = colorList[0]
    private val colorList = arrayOf(Color.parseColor("#00000000"), Color.parseColor("#00DB4437"),Color.parseColor("#00DB4437"),Color.parseColor("#004285F4"),Color.parseColor("#000F9D58"))
    fun randomColor():Int{
        return colorList[(colorList.indices).random()]
    }

}
/*

fun main() {
    val myRandomColor = ColorUtil().randomColor()
    println(myRandomColor)
}*/

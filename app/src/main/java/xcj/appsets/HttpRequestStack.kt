package xcj.appsets

import java.util.*

object HttpRequestStack {
    private var requestPool:Stack<String>?= Stack()
    fun pushRequestToStack(mark:String){
        requestPool?.push(mark)
    }
    fun popFromStackTop():String?{
        if(requestPool?.size==0)
            return null
        return requestPool?.pop()
    }
    fun removeAllRequestFromStack(){
        requestPool?.clear()
    }
    fun getRequestStackSize():Int?{
        return requestPool?.size
    }
}
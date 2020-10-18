package xcj.appsets.model

data class LoginResponseModel(var status:Int,
                              var code:Int,
                              var data:Any){

    override fun toString(): String {
        return "ResponseModel(status=$status, code=$code, data=$data)"
    }
}
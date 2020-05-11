package xcj.appsets.algorithm

object Sort {
    fun bubbleSort(list: ArrayList<Float>) {
        if (list.size == 0) return
        val maxIndex = list.size - 1
        var haveSwap = false        // 标识算法执行过程中是否发生过交换操作
        for (n in 0 until maxIndex) {
            for (i in 0 until maxIndex - n) {
                if (list[i] > list[i + 1]) {
                    swap(list, i, i + 1)
                    haveSwap = true
                }
            }
            if (!haveSwap) return   // 快速结束
        }
    }

    fun swap(list: ArrayList<Float>, index1: Int, index2: Int) {
        val maxIndex = list.size - 1
        val minIndex = 0
        if (index1 < minIndex || index1 > maxIndex) throw IndexOutOfBoundsException()
        if (index2 < minIndex || index2 > maxIndex) throw IndexOutOfBoundsException()
        val tmp = list[index1]
        list[index1] = list[index2]
        list[index2] = tmp
    }
}

/*private operator fun Any.compareTo(any: Any): Int {
    return when(any){
        is Float->{
            if(this>any){1}else{0}
        }
        else->{
            if(this>any){1}else{0}
        }
    }
}*/

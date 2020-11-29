package org.yang.flowdemo

object CodeString {
    const val hello = """
Log.e(tag, "当前线程：主线程")
val job = GlobalScope.launch {
    Log.e(tag, "当前线程：线程id：$ {Thread.currentThread().id}")
    delay(5000)
    Log.e(tag, "协程执行结束")
}
Log.e(tag, "主线程执行结束")

...

// 取消协程
job?.let {
    if (it.isActive) {
        job?.cancel()
        Log.e(tag, "协程取消")
    }
}
    """
}
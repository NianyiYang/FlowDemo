package org.yang.flowdemo

object CodeString {
    const val start = """
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

    const val dispatchers = """
GlobalScope.launch(Dispatchers.IO) {
    Log.e(tag, "调度器从 IO 线程启动")
    Log.e(tag, isMainThread())
}
    """

    const val withContext = """
GlobalScope.launch(Dispatchers.Main) {
    withContext(Dispatchers.IO) {
        Log.e(tag, "切换到 IO 线程")
        Log.e(tag, isMainThread())
    }
    Log.e(tag, "withContext 执行完成后自动切换成主线程")
    Log.e(tag, isMainThread())
}
    """

    const val flowOn = """
GlobalScope.launch(Dispatchers.Main) {
    Log.e(tag, "使用flowOn切换线程")
    flowOf(1)
        .onEach {
            Log.e(tag, "onEach.." + isMainThread())
            delay(1000)
        }
        .flowOn(Dispatchers.IO)
        .map {
            Log.e(tag, "map.." + isMainThread())
            it
        }
        .flowOn(Dispatchers.Main)
        .collect {
            Log.e(tag, "collect.." + isMainThread())
        }
}
    """

    const val error = """
GlobalScope.launch {
    flow {
        emit(1)
        throw RuntimeException()
    }.catch {
        Log.e(tag, "捕获异常")
    }.onCompletion { cause ->
        if (cause == null) {
            Log.e(tag, "异常透明化处理，不会影响下游")
        }
    }.collect {
        Log.e(tag, "执行结束")
    }
}
    """

    const val backPressure = """
GlobalScope.launch {
    testBackPressureFlow()
        .buffer()
//      .conflate()
        .collect {
//      .collectLatest {
            delay(100)
            Log.e(tag, "$ it 已被消费")
        }
}
    """

    const val operator = """
fun onOperator(view: View) {
    GlobalScope.launch {
        mockNetWork1()
            .zip(mockNetWork2()) { a, b ->
                a + b
            }
            .collect {
                Log.e(tag, "a+b = $ it")
            }
    }
}

suspend fun mockNetWork1() = flow {
    delay(2000)
    emit(1)
}

suspend fun mockNetWork2() = flow {
    delay(1000)
    emit(1)
}
    """

    const val md = """
GlobalScope.launch {
    val sum = (1..5).asFlow()
        .map { it * it }
        .reduce { a, b -> a + b }
    // 1 + 4 + 9 + 16 + 25
    Log.e(tag,"平方和 = $ sum")
}
    """
}
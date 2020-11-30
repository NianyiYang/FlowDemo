package org.yang.flowdemo

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private val tag = MainActivity::class.java.simpleName

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_start.showCode(CodeString.start)
        tv_dispatchers.showCode(CodeString.dispatchers)
        tv_with_context.showCode(CodeString.withContext)
        tv_flowOn.showCode(CodeString.flowOn)
        tv_error.showCode(CodeString.error)
        tv_back_pressure.showCode(CodeString.backPressure)
        tv_operator.showCode(CodeString.operator)
        tv_md.showCode(CodeString.md)
    }

    fun startOnGlobalScope(view: View) {
        Log.e(tag, "当前线程：主线程")
        job = GlobalScope.launch {
            Log.e(tag, isMainThread())
            delay(3000)
            Log.e(tag, "协程执行结束")
        }
        Log.e(tag, "主线程执行结束")
    }

    fun cancelOnGlobalScope(view: View) {
        job?.let {
            if (it.isActive) {
                job?.cancel()
                Log.e(tag, "协程取消")
            }
        }
    }

    fun onDispatchers(view: View) {
//        lifecycleScope.launch {
//
//        }
        GlobalScope.launch(Dispatchers.IO) {
            Log.e(tag, "调度器从 IO 线程启动")
            Log.e(tag, isMainThread())
        }
    }

    fun onWithContext(view: View) {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                Log.e(tag, "切换到 IO 线程")
                Log.e(tag, isMainThread())
            }
            Log.e(tag, "withContext 执行完成后自动切换成主线程")
            Log.e(tag, isMainThread())
        }
    }

    fun onFlowOn(view: View) {
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
    }

    fun onError(view: View) {
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
    }

    fun onBackPressure(view: View) {
        GlobalScope.launch {
            testBackPressureFlow()
                .buffer()
                .collect {
                    delay(100)
                    Log.e(tag, "$it 已被消费")
                }
        }
    }

    fun onBackPressure2(view: View) {
        GlobalScope.launch {
            testBackPressureFlow()
                .conflate()
                .collect {
//                .collectLatest {
                    delay(100)
                    Log.e(tag, "$it 已被消费")
                }
        }
    }

    fun onBackPressure3(view: View) {
        GlobalScope.launch {
            testBackPressureFlow()
                .collectLatest {
                    delay(100)
                    Log.e(tag, "$it 已被消费")
                }
        }
    }

    fun onOperator(view: View) {
        GlobalScope.launch {
            mockNetWork1()
                .zip(mockNetWork2()) { a, b ->
                    a + b
                }
                .collect {
                    Log.e(tag, "a+b = $it")
                }
        }
    }

    private suspend fun mockNetWork1() = flow {
        delay(2000)
        emit(1)
    }

    private suspend fun mockNetWork2() = flow {
        delay(1000)
        emit(9)
    }

    @ExperimentalCoroutinesApi
    fun onMd(view: View) {
        GlobalScope.launch {
            val sum = (1..5).asFlow()
                .map { it * it }
                .reduce { a, b -> a + b }
            // 1 + 4 + 9 + 16 + 25
            Log.e(tag, "平方和 = $sum")
        }
    }

    private fun testBackPressureFlow(): Flow<Int> = flow {
        for (i in 1..100) {
            emit(i)
        }
    }

    private fun isMainThread() =
        "当前是否是主线程：${Thread.currentThread() == Looper.getMainLooper().thread}"
}
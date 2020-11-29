package org.yang.flowdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainActivity : AppCompatActivity() {

    private val tag = MainActivity::class.java.simpleName

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_start.showCode(CodeString.hello)
    }

    fun startOnGlobalScope(view: View) {
        Log.e(tag, "当前线程：主线程")
        job = GlobalScope.launch {
            Log.e(tag, "当前线程：线程id：${Thread.currentThread().id}")
            delay(5000)
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
        GlobalScope.launch(Dispatchers.IO) {
            Log.e(tag, "当前线程：线程id：${Thread.currentThread().id}")
            Log.e(tag, "当前是否是主线程：${Thread.currentThread() == Looper.getMainLooper().thread}")
        }
    }

    fun onWithContext(view: View) {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                Log.e(tag, "当前线程：IO 线程id：${Thread.currentThread().id}")
            }
            Log.e(tag, "withContext 执行完成后自动切换成主线程")
            Log.e(tag, "当前是否是主线程：${Thread.currentThread() == Looper.getMainLooper().thread}")
        }
    }

    fun onFlowOn(view: View) {
        GlobalScope.launch(Dispatchers.Main) {
            Log.e(tag, "使用flowOn切换线程")
            flowOf(1)
                .onEach {
                    Log.e(tag, "0. 当前是否是主线程：${Thread.currentThread() == Looper.getMainLooper().thread}")
                    delay(1000)
                }
                .flowOn(Dispatchers.IO)
                .map {
                    Log.e(tag, "1. 当前是否是主线程：${Thread.currentThread() == Looper.getMainLooper().thread}")
                    it
                }
                .flowOn(Dispatchers.Main)
                .map {
                    Log.e(tag, "2. 当前是否是主线程：${Thread.currentThread() == Looper.getMainLooper().thread}")
                    it
                }
                .collect {
                    Log.e(tag, "3. 当前是否是主线程：${Thread.currentThread() == Looper.getMainLooper().thread}")
                }
        }
    }

    fun onError(view: View) {

    }

    fun onBackPressure(view: View) {}
    fun onMd(view: View) {}
}
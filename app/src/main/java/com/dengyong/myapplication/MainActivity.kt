package com.dengyong.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dengyong.myapplication.proxy.InvocationHandlerImpl
import com.dengyong.myapplication.proxy.ProxyImpl
import com.dengyong.myapplication.proxy.ProxyInterface
import com.dengyong.pluginframework.AppPluginHelper
import java.lang.reflect.Proxy

class MainActivity : AppCompatActivity() {
    private var isLoadPlugin = false

    private val serviceConnection = object :ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //加载插件
    fun load(view: View) {
        loadClass()
    }

    //使用插件里面的类方法
    fun click(view: View) {
        loadClass()
        val toastU = classLoader.loadClass("com.dengyong.pluginapp.utils.ToastUtils")
        val toast = toastU.getDeclaredMethod("toast", Context::class.java)
        toast.isAccessible = true
        toast.invoke(toastU.newInstance(), this.applicationContext)
    }

    //跳转插件里面的Activity
    fun startPlugin(view: View) {
        loadClass()
        val  pluginActivity = Class.forName("com.dengyong.pluginapp.PluginActivity")
        val intent = Intent(this,pluginActivity)
        startActivity(intent)
    }

    //动态代理：代理的是接口，然后用代理对象执行接口方法，InvocationHandlerImpl类里面会对该方法进行拦截，
    // 步骤 创建一个接口，一个类实现这个接口，得到实现类的示例，创建也好在别的类里面获取也好，
    //     然后掉用Proxy.newProxyInstance创建动态代理对象，最后用代理对象调用方法就行
    fun daili(view: View) {
        val proxyImpl = ProxyImpl()
        val proxy = Proxy.newProxyInstance(
            classLoader,
            arrayOf(ProxyInterface::class.java),
            InvocationHandlerImpl(proxyImpl)
        ) as ProxyInterface
        proxy.getLog("测试动态代理")
    }

    fun startService(view:View){
        loadClass()
        val i = Intent(this.applicationContext, Class.forName("com.dengyong.pluginapp.PluginService"))
        startService(i)
    }

    private fun loadClass(){
        if (!isLoadPlugin){
            AppPluginHelper.getHelper(this).loadPluginClass("${this.getExternalFilesDir(null)?.path}/pluginapp-debug.apk")
            isLoadPlugin = true
        }
    }
}
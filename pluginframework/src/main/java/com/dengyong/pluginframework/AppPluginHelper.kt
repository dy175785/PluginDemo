package com.dengyong.pluginframework

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Handler
import android.os.Message
import android.util.Log
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader
import java.lang.reflect.Array
import java.lang.reflect.Proxy
import java.util.ArrayList

/**
 * @Description
 * @Author DengYong
 * @Time 2022/4/19 10:39
 */
class AppPluginHelper private constructor(val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        private lateinit var HELPER: AppPluginHelper

        @JvmStatic
        fun getHelper(context: Context): AppPluginHelper {
            if (!this::HELPER.isInitialized) {
                synchronized(AppPluginHelper::class.java) {
                    if (!this::HELPER.isInitialized) {
                        HELPER = AppPluginHelper(context)
                    }
                }
            }
            return HELPER
        }
    }

    /**
     * 合并dex文件
     */
    fun loadPluginClass(apkPath: String):AppPluginHelper {
        val baseClassLoader = Class.forName("dalvik.system.BaseDexClassLoader")
        val pathClassLoader = context.classLoader as PathClassLoader

        val appPathListField = baseClassLoader.getDeclaredField("pathList")
        appPathListField.isAccessible = true
        //获取pathList 在baseClassLoader中获取pathList
        val appPathList = appPathListField.get(pathClassLoader)

        val appDexElementsField = appPathList.javaClass.getDeclaredField("dexElements")
        appDexElementsField.isAccessible = true
        //获取.dex文件的数组集合 在pathList对象中获取dexElements
        val appDexElements = appDexElementsField.get(appPathList)

        val pluginClassLoader =
            DexClassLoader(apkPath, context.cacheDir.absolutePath, null, context.classLoader)
        //插件的pathList 在baseClassLoader中获取pathList
        val pluginPathList = appPathListField.get(pluginClassLoader)
        val pluginElements = appDexElementsField.get(pluginPathList)
        //重组dex数组
        val mainLength = Array.getLength(appDexElements)
        val pluginLength = Array.getLength(pluginElements)
        val newArray = Array.newInstance(
            appDexElements.javaClass.componentType,
            mainLength + pluginLength
        )
        System.arraycopy(appDexElements, 0, newArray, 0, mainLength)
        System.arraycopy(pluginElements, 0, newArray, mainLength, pluginLength)
        //设置新数组
        appDexElementsField.set(appPathList, newArray)
        return this
    }

    /**
     * 加载Apk的Resource的资源
     */
    fun loadResources(apkPath: String): Resources {
        val assetManager = AssetManager::class.java.newInstance()
        val addPathMethod =
            assetManager.javaClass.getDeclaredMethod("addAssetPath", String::class.java)
        addPathMethod.isAccessible = true
        addPathMethod.invoke(assetManager, apkPath)
        return Resources(
            assetManager,
            context.resources.displayMetrics,
            context.resources.configuration
        )
    }

    /**
     * hook startActivity 先得到AMS 把 AMS里面的意图替换成占坑的Activity
     * 然后再在ActivityThreads里面把占坑的Activity替换成真正想要跳转的Activity
     *
     * AMS 实例存放在ActivityTaskManager IActivityTaskManagerSingleton变量里面的get方法中  每个SDK版本不一致，根据源码来写
     */
    fun hookStartActivity(classProxy: Class<out Activity>):AppPluginHelper {
        val atmClass = Class.forName("android.app.ActivityTaskManager")
        val iActivityTaskManagerSingleton =
            atmClass.getDeclaredField("IActivityTaskManagerSingleton")
        iActivityTaskManagerSingleton.isAccessible = true
        //获取静态变量可以直接为null
        val atmSingleton = iActivityTaskManagerSingleton.get(null)

        val singletonClass = Class.forName("android.util.Singleton");
        val mInstance = singletonClass.getDeclaredField("mInstance")
        mInstance.isAccessible = true
        //获取根据Singleton类 的get方法AMS的对象
        val getMethod = singletonClass.getMethod("get")
        val amsValue = getMethod.invoke(atmSingleton)

        val iActivityTaskManagerClass = Class.forName("android.app.IActivityTaskManager")
        val ams = Proxy.newProxyInstance(
            context.classLoader, arrayOf(iActivityTaskManagerClass),
            HookInvocationHandlerActivityImpl(
                amsValue,
                context,
                classProxy
            )
        )
        //将代理对象设置给singleton类里面的mInstance
        mInstance.set(atmSingleton, ams)
        return this
    }

    fun hookStartServices(classProxy: Class<out Service>):AppPluginHelper{
        val amClass = Class.forName("android.app.ActivityManager")
        val iActivityManagerSingleton = amClass.getDeclaredField("IActivityManagerSingleton")
        iActivityManagerSingleton.isAccessible = true
        val amSingleton = iActivityManagerSingleton.get(null)
        val singletonClass = Class.forName("android.util.Singleton");
        val mInstance = singletonClass.getDeclaredField("mInstance")
        mInstance.isAccessible = true
        //获取根据Singleton类 的get方法AMS的对象
        val getMethod = singletonClass.getMethod("get")
        val amValue = getMethod.invoke(amSingleton)

        val iActivityManagerClass = Class.forName("android.app.IActivityManager")
        val ams = Proxy.newProxyInstance(
            context.classLoader, arrayOf(iActivityManagerClass),
            HookInvocationHandlerServiceImpl(
                amValue,
                context,
                classProxy
            )
        )
        //将代理对象设置给singleton类里面的mInstance
        mInstance.set(amSingleton, ams)
        return this
    }

    //总体逻辑就是拿到ActivityThread对象，再获取到里面创建Activity的Handler，再对Handler里面的mCallBack进行替换
    // 对Handler里面传的LaunchActivityItem里的Intent进行替换
    fun hookLaunchActivity():AppPluginHelper {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val activityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread")
        activityThreadField.isAccessible = true
        //静态变量直接get(null)就可以了
        val activityThread = activityThreadField.get(null)
        //再获取handler
        val mHField = activityThreadClass.getDeclaredField("mH")
        mHField.isAccessible = true
        val mH = mHField.get(activityThread)
        //获取handler里面的CallBack
        val handlerClass = Class.forName("android.os.Handler")
        val mCallBackField = handlerClass.getDeclaredField("mCallback")
        mCallBackField.isAccessible = true
        //设置Handler对象里面的mCallBack成员变量设置新值,
        // 参数第一个填你要给哪个Handler对象的mCallBack设置新值
        mCallBackField.set(mH, HandlerCallBack())
        return this
    }

    /**
     * 用来修改ActivityThread内部的mH变量的HandlerCallBacks
     */
    private inner class HandlerCallBack : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            if (msg.what == 159) {
                handleLaunchActivity(msg)
            }
            if (msg.what == 115){
                Log.e("HookUtils:",msg.obj.toString())
            }
            return false
        }
    }

    /**
     * 对启动的Activity由原来占坑的Activity修改成正式目的的Activity
     */
    private fun handleLaunchActivity(msg: Message) {
        val clientTransaction = msg.obj
        val mActivityCallBacksField =
            clientTransaction.javaClass.getDeclaredField("mActivityCallbacks")
        mActivityCallBacksField.isAccessible = true
        val mActivityCallBacks = mActivityCallBacksField.get(clientTransaction)

        if (mActivityCallBacks is ArrayList<*>) {
            val launcherActivityItemClass =
                Class.forName("android.app.servertransaction.LaunchActivityItem")
            mActivityCallBacks.forEachIndexed { index, i ->
                if (i.javaClass == launcherActivityItemClass) {
                    val newIntentField = i.javaClass.getDeclaredField("mIntent")
                    newIntentField.isAccessible = true
                    val newIntent = newIntentField.get(i)
                    if (newIntent is Intent) {
                        val oldIntent =
                            newIntent.getParcelableExtra<Intent>(HookInvocationHandlerActivityImpl.OLD_INTENT)
                        oldIntent?.let {
                            newIntent.component = it.component
                        }
                    }
                }
            }
        }
    }

    private fun handleLaunchServices(msg: Message){

    }

}
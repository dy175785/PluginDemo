package com.dengyong.myapplication.proxy

import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * @Description
 * @Author DengYong
 * @Time 2022/4/14 10:56
 */
class InvocationHandlerImpl(val proxy: ProxyInterface) : InvocationHandler {

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        args?.let {
            for (i in it){
                Log.e("Proxy-------------",i.toString())
            }
        }
        return method?.invoke(this.proxy, args?.get(0) ?: "hhhhh")
    }
}
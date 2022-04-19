package com.dengyong.pluginframework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
  * @Description
  * @Author DengYong
  * @Time 2022/4/14 15:29
  */
public class HookInvocationHandlerActivityImpl implements InvocationHandler {
    public static final String OLD_INTENT = "old_intent";
    private Object ams;
    private Class proxyClass;
    private Context context;
    public HookInvocationHandlerActivityImpl(Object o, Context context, Class<? extends Activity> proxyClass) {
        this.ams = o;
        this.proxyClass = proxyClass;
        this.context = context;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("startActivity")){
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent){
                    Intent oldIntent = (Intent) args[i];
                    Intent newIntent = new Intent(context,proxyClass);
                    newIntent.putExtra(OLD_INTENT,oldIntent);
                    args[i] = newIntent;
                }
            }
        }
        return method.invoke(ams,args);
    }
}

package com.dengyong.pluginapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.dengyong.pluginframework.base.BaseActivity
import com.dengyong.pluginframework.base.BaseResourcesHelper

class PluginActivity : BaseActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        loadResource(newBase?.getExternalFilesDir(null)?.path + "/pluginapp-debug.apk")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("Plugin", "这是插件里面的Activity")
        startService(Intent(this,PluginService::class.java))
    }

    override fun loadResource(apkPath: String?) {
        BaseResourcesHelper.getInstance(this.applicationContext).setResource(apkPath)
    }

    fun test(view: View) {
        startActivity(Intent(this,Plugin2Activity::class.java))
    }
}
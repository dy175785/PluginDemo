package com.dengyong.pluginapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dengyong.pluginframework.base.BaseActivity

class Plugin2Activity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plugin2)
    }

    override fun loadResource(apkPath: String?) {

    }
}
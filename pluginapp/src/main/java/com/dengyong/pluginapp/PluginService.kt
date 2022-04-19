package com.dengyong.pluginapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class PluginService : Service() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("TAG", "onStartCommand:------------- ")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
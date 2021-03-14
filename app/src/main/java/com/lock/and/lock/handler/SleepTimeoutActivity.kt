package com.lock.and.lock.handler

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.System.canWrite
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.lock.and.lock.R

class SleepTimeoutActivity : Activity() {

    private val timeout by lazy { Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 60000) }
    private val stayOnWhilePluggedIn by lazy { Settings.Global.getInt(contentResolver, Settings.Global.STAY_ON_WHILE_PLUGGED_IN, 0) }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeout
        stayOnWhilePluggedIn
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.decorView.setBackgroundColor(resources.getColor(R.color.black))
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        putSettings(0, 0)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (!hasFocus) {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()

        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDestroy() {
        super.onDestroy()
        putSettings(timeout, stayOnWhilePluggedIn)
    }

    override fun onBackPressed() {

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun putSettings(timeout: Int, stayOnWhilePluggedIn: Int) {
        if (!canWrite(this)) return
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, timeout)
        Settings.System.putInt(contentResolver, Settings.Global.STAY_ON_WHILE_PLUGGED_IN, stayOnWhilePluggedIn)
        Log.d("SleepTimeoutActivity", "Screen timeout settings set to $timeout $stayOnWhilePluggedIn")
    }
}
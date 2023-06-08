package com.example.service

import android.content.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.service.databinding.ActivityMainBinding
import com.example.service.services.BoundService
import com.example.service.services.ForegroundService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var mService: BoundService

    private var mBound: Boolean = false
    private var boundJob: Job? = null

    private val boundConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as BoundService.BoundBinder
            mService = binder.getService()
            boundJob = lifecycleScope.launch {
                mService.values.collect {
                    Log.d("BS", it.toString())
                    binding.bsText.text = it.toString()
                }
            }
            mBound = true
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            mBound = false
            boundJob?.cancel()
        }
    }


    private val foregroundConnection = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.extras != null) {
                intent.extras?.getLong(FOREGROUND_KEY)
                    ?.let {
                        Log.d("FS", it.toString())
                        binding.fsText.text = it.toString()
                    }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        broadcastManager = LocalBroadcastManager.getInstance(this@MainActivity)

        binding.bsBtn.setOnClickListener {
            if (!BoundService.isConnected) {
                Intent(this@MainActivity, BoundService::class.java).also { intent ->
                    bindService(intent, boundConnection, Context.BIND_AUTO_CREATE)
                }
            }
        }

        binding.fsBtn.setOnClickListener {
            if (!ForegroundService.isConnected) {
                Intent(this@MainActivity, ForegroundService::class.java).also { intent ->
                    startForegroundService(intent)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        broadcastManager.registerReceiver(foregroundConnection, IntentFilter(INTENT_FILTER))
    }

    override fun onStop() {
        super.onStop()
        broadcastManager.unregisterReceiver(foregroundConnection)
        if (mBound) {
            unbindService(boundConnection)
        }
    }

    companion object {
        const val FOREGROUND_KEY = "foreground_number"
        const val INTENT_FILTER = "intent_filter"
    }
}
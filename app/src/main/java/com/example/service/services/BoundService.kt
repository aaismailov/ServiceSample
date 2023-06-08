package com.example.service.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.service.FibonacciCalculator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BoundService : Service() {

    private val binder = BoundBinder()
    private val _values = MutableStateFlow(0L)
    val values = _values.asStateFlow()

    inner class BoundBinder : Binder() {
        fun getService() : BoundService = this@BoundService
    }

    override fun onBind(intent: Intent): IBinder {
        isConnected = true
        val calculator = FibonacciCalculator()
        CoroutineScope(Dispatchers.Main + SupervisorJob())
            .launch {
                while (isConnected) {
                    delay(1000)
                    _values.emit(calculator.getNextNumber())
                }
        }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isConnected = false
        return super.onUnbind(intent)
    }

    companion object {
        var isConnected = false
    }
}
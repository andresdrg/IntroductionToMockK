package com.example.mockkexample

import android.util.Log
import kotlinx.coroutines.delay

class Tracker {

    companion object {
        private const val TAG = "Tracker"
    }

    fun sendMessage(message: Message) {
        Log.d(
            TAG,
            "text: ${message.text}, code: ${message.code}"
        )
    }

    suspend fun sendCode(code: Int) {
        delay(2_500)
        Log.d(
            TAG,
            "$code"
        )
    }
}
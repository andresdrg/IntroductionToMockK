package com.example.mockkexample

import android.util.Log

class Logger {

    companion object {
        private const val TAG = "Logger"

        fun getLongLogTag() = TAG + "_COMPANION_OBJECT"
    }

    fun log(message: String) {
        Log.d(
            TAG,
            message
        )
    }

    fun logGetMessage(message: String): String {
        Log.d(
            TAG,
            message
        )
        return message
    }

    fun logGetLongTag(message: String): String {
        val logTag = getLongLogTag()
        Log.d(
            logTag,
            message
        )
        return logTag
    }
}
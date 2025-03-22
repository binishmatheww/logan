package com.binishmatheww.logan

import android.util.Log
import java.util.regex.Pattern

/**
 * Created by Binish Mathew in March 2025.
 *
 * email: mail@bini.sh, binishmatheww@gmail.com
 *
 * website: https://bini.sh
 */
class AndroidPlatform: Platform {

    private val anonymousClass = Pattern.compile("(\\$\\d+)+$")

    override fun nativeLog(
        level: LoganLevel,
        tag: String,
        message: String,
        continuation: Boolean
    ) {
        when (level) {
            LoganLevel.Verbose -> {
                Log.v(
                    tag,
                    message
                )
            }
            LoganLevel.Debug -> {
                Log.d(
                    tag,
                    message
                )
            }
            LoganLevel.Info -> {
                Log.i(
                    tag,
                    message
                )
            }
            LoganLevel.Warn -> {
                Log.w(
                    tag,
                    message
                )
            }
            LoganLevel.Error -> {
                Log.e(
                    tag,
                    message
                )
            }
        }
    }

    override fun getCallerNameFromStackTrace(): String? {
        val thread = Thread.currentThread().stackTrace
        return if (thread.size >= 9) {
            thread[9].run {
                var refinedCallerName = className
                val m = anonymousClass.matcher(refinedCallerName)
                if (m.find()) {
                    refinedCallerName = m.replaceAll("")
                }
                refinedCallerName = refinedCallerName.substring(refinedCallerName.lastIndexOf('.') + 1)
                "${refinedCallerName}\$$methodName"
            }
        } else {
            null
        }
    }

    override fun getStackTraceString(
        throwable: Throwable?
    ): String {
        return Log.getStackTraceString(throwable)
    }

}

actual fun getPlatform(): Platform = AndroidPlatform()
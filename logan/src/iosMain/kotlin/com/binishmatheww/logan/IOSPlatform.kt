package com.binishmatheww.logan

import platform.Foundation.NSLog
import platform.Foundation.NSThread

/**
 * Created by Binish Mathew in March 2025.
 *
 * email: binishmatheww@gmail.com
 *
 * website: https://bini.sh
 */
class IOSPlatform: Platform {

    override fun nativeLog(
        level: LoganLevel,
        tag: String,
        message: String,
        continuation: Boolean
    ) {
        when (level) {
            LoganLevel.Verbose -> {
                NSLog(
                    "${getCurrentTimeAsFormattedString().uppercase()}\t${tag}\t[V]\t$message"
                )
            }
            LoganLevel.Debug -> {
                NSLog(
                    "${getCurrentTimeAsFormattedString().uppercase()}\t${tag}\t${"[D]\t${message}"}"
                )
            }
            LoganLevel.Info -> {
                NSLog(
                    "${getCurrentTimeAsFormattedString().uppercase()}\t${tag}\t${"[I]\t${message}"}"
                )
            }
            LoganLevel.Warn -> {
                NSLog(
                    "${getCurrentTimeAsFormattedString().uppercase()}\t${tag}\t${"[W]\t${message}"}"
                )
            }
            LoganLevel.Error -> {
                NSLog(
                    "${getCurrentTimeAsFormattedString().uppercase()}\t${tag}\t${"[E]\t${message}"}"
                )
            }
        }
    }

    override fun getCallerNameFromStackTrace(): String? {
        val symbols = NSThread.callStackSymbols
        if (symbols.size <= 8) return null
        var refinedCallerName = symbols[8] as? String ?: return null
        refinedCallerName = refinedCallerName.substringBeforeLast('$')
        refinedCallerName = refinedCallerName.substringBeforeLast('(')
        if (refinedCallerName.contains("$")) {
            // coroutines
            refinedCallerName = refinedCallerName.substring(refinedCallerName.lastIndexOf(".", refinedCallerName.lastIndexOf(".") - 1) + 1)
            refinedCallerName = refinedCallerName.replace("$", "")
            refinedCallerName = refinedCallerName.replace("COROUTINE", if (true/*coroutinesSuffix*/) "[async]" else "")
        } else {
            // others
            refinedCallerName = refinedCallerName.substringAfterLast(".")
            refinedCallerName = refinedCallerName.replace("#", ".")
        }
        return refinedCallerName
    }

    override fun getStackTraceString(
        throwable: Throwable?
    ): String {
        return throwable?.stackTraceToString().orEmpty()
    }

}

actual fun getPlatform(): Platform = IOSPlatform()
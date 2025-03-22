package com.binishmatheww.logan

import com.binishmatheww.logan.utilities.FastPrintWriter
import java.io.StringWriter
import java.net.UnknownHostException
import java.util.regex.Pattern

/**
 * Created by Binish Mathew in March 2025.
 *
 * email: mail@bini.sh, binishmatheww@gmail.com
 *
 * website: https://bini.sh
 */
class JvmPlatform: Platform {

    private val anonymousClassPattern = Pattern.compile("(\\$\\d+)+$")

    override fun nativeLog(
        level: LoganLevel,
        tag: String,
        message: String,
        continuation: Boolean
    ){
        when (level) {
            LoganLevel.Verbose -> {
                if(continuation) print(message)
                else println(
                    "${getCurrentTimeAsFormattedString().uppercase()}\t${tag}\t[V]\t$message"
                )
            }
            LoganLevel.Debug -> {
                if(continuation) print(message)
                else println(
                    "${getCurrentTimeAsFormattedString().uppercase()}\t${tag}\t${"[D]\t${message}"}"
                )
            }
            LoganLevel.Info -> {
                if(continuation) print(message)
                else println(
                    "${getCurrentTimeAsFormattedString().uppercase()}\t${tag}\t${"[I]\t${message}"}"
                )
            }
            LoganLevel.Warn -> {
                if(continuation) print(message)
                else println(
                    "${getCurrentTimeAsFormattedString().uppercase()}\t${tag}\t${"[W]\t${message}"}"
                )
            }
            LoganLevel.Error -> {
                if(continuation) print(message)
                else println(
                    "${getCurrentTimeAsFormattedString().uppercase()}\t${tag}\t${"[E]\t${message}"}"
                )
            }
        }
    }

    override fun getCallerNameFromStackTrace(): String? {
        val thread = Thread.currentThread().stackTrace
        return if (thread.size >= 8) {
            thread[8].run {
                var refinedCallerName = className
                val m = anonymousClassPattern.matcher(refinedCallerName)
                if (m.find()) {
                    refinedCallerName = m.replaceAll("")
                }
                refinedCallerName = refinedCallerName.substring(refinedCallerName.lastIndexOf('.') + 1)
                "${refinedCallerName}\$$methodName"
            }
        }
        else {
            null
        }
    }

    override fun getStackTraceString(
        throwable: Throwable?
    ): String {
        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        var t = throwable
        while (t != null) {
            if (t is UnknownHostException) {
                return ""
            }
            t = t.cause
        }
        val sw = StringWriter()
        val pw = FastPrintWriter(sw, false, 256)
        throwable?.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

}

actual fun getPlatform(): Platform = JvmPlatform()
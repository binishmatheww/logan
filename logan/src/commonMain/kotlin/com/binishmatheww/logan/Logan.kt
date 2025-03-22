package com.binishmatheww.logan

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char

/**
 * Created by Binish Mathew in March 2025.
 *
 * email: binishmatheww@gmail.com
 *
 * website: https://bini.sh
 */
object Logan {

    private val platform = getPlatform()

    private const val TAG = "Logan"

    val currentDateFormat = LocalDateTime.Format {
        dayOfMonth(); char('-'); monthNumber(); char('-'); year()
        char(' ')
        amPmHour(); char(':'); minute(); char(':'); second(); char(' '); amPmMarker("am", "pm")
    }

    fun v(
        message: String
    ) {
        log(
            level = LoganLevel.Verbose,
            tag = getTag(),
            message = message,
            continuation = false
        )
    }

    fun v(
        tag: String = getTag(),
        message: String
    ) {
        log(
            level = LoganLevel.Verbose,
            tag = tag,
            message = message,
            continuation = false
        )
    }

    fun d(
        message: String
    ) {
        log(
            level = LoganLevel.Debug,
            tag = getTag(),
            message = message,
            continuation = false
        )
    }

    fun d(
        tag: String = getTag(),
        message: String
    ) {
        log(
            level = LoganLevel.Debug,
            tag = tag,
            message = message,
            continuation = false
        )
    }

    fun i(
        message: String
    ) {
        log(
            level = LoganLevel.Info,
            tag = getTag(),
            message = message,
            continuation = false
        )
    }

    fun i(
        tag: String = getTag(),
        message: String
    ) {
        log(
            level = LoganLevel.Info,
            tag = tag,
            message = message,
            continuation = false
        )
    }

    fun w(
        message: String,
    ) {
        log(
            level = LoganLevel.Warn,
            tag = getTag(),
            message = message,
            continuation = false
        )
    }

    fun w(
        tag: String = getTag(),
        message: String,
    ) {
        log(
            level = LoganLevel.Warn,
            tag = tag,
            message = message,
            continuation = false
        )
    }

    fun w(
        tag: String = getTag(),
        message: String,
        throwable: Throwable? = null
    ) {
        log(
            level = LoganLevel.Warn,
            tag = tag,
            message = message + '\n' + platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun w(
        tag: String = getTag(),
        throwable: Throwable? = null
    ) {
        log(
            level = LoganLevel.Warn,
            tag = tag,
            message = platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun e(
        tag: String = getTag(),
        message: String
    ) {
        log(
            level = LoganLevel.Error,
            tag = tag,
            message = message,
            continuation = false
        )
    }


    fun e(
        tag: String = getTag(),
        message: String,
        throwable: Throwable? = null
    ) {
        log(
            level = LoganLevel.Error,
            tag = tag,
            message = message + '\n' + platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun e(
        message: String
    ) {
        log(
            level = LoganLevel.Error,
            tag = getTag(),
            message = message,
            continuation = false
        )
    }


    fun e(
        tag: String = getTag(),
        throwable: Throwable?
    ) {
        log(
            level = LoganLevel.Error,
            tag = tag,
            message = platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun e(
        throwable: Throwable?
    ) {
        log(
            level = LoganLevel.Error,
            tag = getTag(),
            message = platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun wtf(
        tag: String = getTag(),
        message: String
    ) {
        log(
            level = LoganLevel.Error,
            tag = tag,
            message = message,
            continuation = false
        )
    }


    fun wtf(
        tag: String = getTag(),
        message: String,
        throwable: Throwable? = null
    ) {
        log(
            level = LoganLevel.Error,
            tag = tag,
            message = message + '\n' + platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun wtf(
        message: String
    ) {
        log(
            level = LoganLevel.Error,
            tag = getTag(),
            message = message,
            continuation = false
        )
    }


    fun wtf(
        tag: String = getTag(),
        throwable: Throwable?
    ) {
        log(
            level = LoganLevel.Error,
            tag = tag,
            message = platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun wtf(
        throwable: Throwable?
    ) {
        log(
            level = LoganLevel.Error,
            tag = getTag(),
            message = platform.getStackTraceString(throwable),
            continuation = false
        )
    }

    fun log(
        level: LoganLevel,
        tag: String,
        message: String,
        continuation: Boolean = false
    ) {
        if (message.length > 3000) {
            platform.nativeLog(
                level = level,
                tag = tag,
                message = message.take( 3000),
                continuation = continuation
            )
            log(
                level = level,
                tag = tag,
                message = message.drop( 3000),
                continuation = true
            )
        }
        else {
            platform.nativeLog(
                level = level,
                tag = tag,
                message = message,
                continuation = continuation
            )
        }
    }

    fun getTag(): String {
        return platform.getCallerNameFromStackTrace() ?: TAG
    }

}
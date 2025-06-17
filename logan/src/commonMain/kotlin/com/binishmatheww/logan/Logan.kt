package com.binishmatheww.logan

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char

/**
 * Created by Binish Mathew in March 2025.
 *
 * email: mail@bini.sh, binishmatheww@gmail.com
 *
 * website: https://bini.sh
 */
object Logan {

    private var isLoggingEnabled = true

    private val platform = getPlatform()

    private const val TAG = "Logan"

    val currentDateFormat = LocalDateTime.Format {
        dayOfMonth(); char('-'); monthNumber(); char('-'); year()
        char(' ')
        amPmHour(); char(':'); minute(); char(':'); second(); char(' '); amPmMarker("am", "pm")
    }

    fun toggleLogging(
        isLoggingEnabled: Boolean
    ) {
        this.isLoggingEnabled = isLoggingEnabled
    }

    fun v(
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Verbose,
            tag = getTag(),
            message = message.invoke(),
            continuation = false
        )
    }

    fun v(
        tag: () -> String = { getTag() },
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Verbose,
            tag = tag.invoke(),
            message = message.invoke(),
            continuation = false
        )
    }

    fun d(
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Debug,
            tag = getTag(),
            message = message.invoke(),
            continuation = false
        )
    }

    fun d(
        tag: () -> String = { getTag() },
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Debug,
            tag = tag.invoke(),
            message = message.invoke(),
            continuation = false
        )
    }

    fun i(
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Info,
            tag = getTag(),
            message = message.invoke(),
            continuation = false
        )
    }

    fun i(
        tag: () -> String = { getTag() },
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Info,
            tag = tag.invoke(),
            message = message.invoke(),
            continuation = false
        )
    }

    fun w(
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Warn,
            tag = getTag(),
            message = message.invoke(),
            continuation = false
        )
    }

    fun w(
        tag: () -> String = { getTag() },
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Warn,
            tag = tag.invoke(),
            message = message.invoke(),
            continuation = false
        )
    }

    fun w(
        tag: () -> String = { getTag() },
        message: () -> String,
        throwable: Throwable? = null
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Warn,
            tag = tag.invoke(),
            message = message.invoke() + '\n' + platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun w(
        tag: () -> String = { getTag() },
        throwable: Throwable? = null
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Warn,
            tag = tag.invoke(),
            message = platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun e(
        tag: () -> String = { getTag() },
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Error,
            tag = tag.invoke(),
            message = message.invoke(),
            continuation = false
        )
    }


    fun e(
        tag: () -> String = { getTag() },
        message: () -> String,
        throwable: Throwable? = null
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Error,
            tag = tag.invoke(),
            message = message.invoke() + '\n' + platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun e(
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Error,
            tag = getTag(),
            message = message.invoke(),
            continuation = false
        )
    }


    fun e(
        tag: () -> String = { getTag() },
        throwable: Throwable?
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Error,
            tag = tag.invoke(),
            message = platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun e(
        throwable: Throwable?
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Error,
            tag = getTag(),
            message = platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun wtf(
        tag: () -> String = { getTag() },
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Error,
            tag = tag.invoke(),
            message = message.invoke(),
            continuation = false
        )
    }


    fun wtf(
        tag: () -> String = { getTag() },
        message: () -> String,
        throwable: Throwable? = null
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Error,
            tag = tag.invoke(),
            message = message.invoke() + '\n' + platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun wtf(
        message: () -> String
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Error,
            tag = getTag(),
            message = message.invoke(),
            continuation = false
        )
    }


    fun wtf(
        tag: () -> String = { getTag() },
        throwable: Throwable?
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
        log(
            level = LoganLevel.Error,
            tag = tag.invoke(),
            message = platform.getStackTraceString(throwable),
            continuation = false
        )
    }


    fun wtf(
        throwable: Throwable?
    ) {
        if(isLoggingEnabled.not()) {
            return
        }
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
        if(isLoggingEnabled.not()) {
            return
        }
        if (message.length > 3000) {
            platform.nativeLog(
                level = level,
                tag = tag,
                message = message.take(3000),
                continuation = continuation
            )
            log(
                level = level,
                tag = tag,
                message = message.drop(3000),
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
package com.binishmatheww.logan

import com.binishmatheww.logan.Logan.currentDateFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

/**
 * Created by Binish Mathew in March 2025.
 *
 * email: mail@bini.sh, binishmatheww@gmail.com
 *
 * website: https://bini.sh
 */
interface Platform {

    fun nativeLog(
        level: LoganLevel,
        tag: String,
        message: String,
        continuation: Boolean
    )

    fun getCallerNameFromStackTrace(): String?

    fun getStackTraceString(
        throwable: Throwable?
    ): String

    fun getCurrentTimeAsFormattedString(): String {
        return Instant
            .fromEpochMilliseconds(
                Clock.System.now().toEpochMilliseconds()
            )
            .toLocalDateTime(
                TimeZone.currentSystemDefault()
            )
            .format(
                currentDateFormat
            )
    }

}

expect fun getPlatform(): Platform
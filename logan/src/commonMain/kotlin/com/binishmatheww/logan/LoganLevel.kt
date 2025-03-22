package com.binishmatheww.logan

/**
 * Created by Binish Mathew in March 2025.
 *
 * email: mail@bini.sh, binishmatheww@gmail.com
 *
 * website: https://bini.sh
 */
sealed class LoganLevel {
    object Verbose: LoganLevel()
    object Debug: LoganLevel()
    object Info: LoganLevel()
    object Warn: LoganLevel()
    object Error: LoganLevel()
}
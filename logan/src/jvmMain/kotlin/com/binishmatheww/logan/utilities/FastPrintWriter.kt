/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.binishmatheww.logan.utilities

import com.binishmatheww.logan.Logan
import java.io.*
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CharsetEncoder
import java.nio.charset.CodingErrorAction

/**
 * Copied shamelessly from The Android Open Source Project by Binish Mathew in March 2025.
 *
 * All credit goes to the great maintainers of The Android Open Source Project and the Android team at Google.
 *
 * email: mail@bini.sh, binishmatheww@gmail.com
 *
 * website: https://bini.sh
 */
class FastPrintWriter : PrintWriter {

    private class DummyWriter : Writer() {
        override fun close() {
            throw UnsupportedOperationException("Shouldn't be here")
        }

        @Throws(IOException::class)
        override fun flush() {
            close()
        }

        @Throws(IOException::class)
        override fun write(buf: CharArray, offset: Int, count: Int) {
            close()
        }
    }

    private val mBufferLen: Int
    private val mText: CharArray
    private var mPos = 0
    private val mOutputStream: OutputStream?
    private val mAutoFlush: Boolean
    private val mSeparator: String
    private val mWriter: Writer?
    private val mPrinter: Printer?
    private var mCharset: CharsetEncoder? = null
    private val mBytes: ByteBuffer?
    private var mIoError = false

    @JvmOverloads
    constructor(out: OutputStream?, autoFlush: Boolean = false, bufferLen: Int = 8192) : super(
        DummyWriter(),
        autoFlush
    ) {
        if (out == null) {
            throw NullPointerException("out is null")
        }
        mBufferLen = bufferLen
        mText = CharArray(bufferLen)
        mBytes = ByteBuffer.allocate(mBufferLen)
        mOutputStream = out
        mWriter = null
        mPrinter = null
        mAutoFlush = autoFlush
        mSeparator = System.lineSeparator()
        initDefaultEncoder()
    }

    @JvmOverloads
    constructor(wr: Writer?, autoFlush: Boolean = false, bufferLen: Int = 8192) : super(DummyWriter(), autoFlush) {
        if (wr == null) {
            throw NullPointerException("wr is null")
        }
        mBufferLen = bufferLen
        mText = CharArray(bufferLen)
        mBytes = null
        mOutputStream = null
        mWriter = wr
        mPrinter = null
        mAutoFlush = autoFlush
        mSeparator = System.lineSeparator()
        initDefaultEncoder()
    }

    @JvmOverloads
    constructor(pr: Printer?, bufferLen: Int = 512) : super(DummyWriter(), true) {
        if (pr == null) {
            throw NullPointerException("pr is null")
        }
        mBufferLen = bufferLen
        mText = CharArray(bufferLen)
        mBytes = null
        mOutputStream = null
        mWriter = null
        mPrinter = pr
        mAutoFlush = true
        mSeparator = System.lineSeparator()
        initDefaultEncoder()
    }

    @Throws(UnsupportedEncodingException::class)
    private fun initEncoder(csn: String) {
        mCharset = try {
            Charset.forName(csn).newEncoder()
        } catch (e: Exception) {
            throw UnsupportedEncodingException(csn)
        }
        mCharset?.onMalformedInput(CodingErrorAction.REPLACE)
        mCharset?.onUnmappableCharacter(CodingErrorAction.REPLACE)
    }

    /**
     * Flushes this writer and returns the value of the error flag.
     *
     * @return `true` if either an `IOException` has been thrown
     * previously or if `setError()` has been called;
     * `false` otherwise.
     * @see .setError
     */
    override fun checkError(): Boolean {
        flush()
        synchronized(lock) { return mIoError }
    }

    /**
     * Sets the error state of the stream to false.
     * @since 1.6
     */
    override fun clearError() {
        synchronized(lock) { mIoError = false }
    }

    /**
     * Sets the error flag of this writer to true.
     */
    override fun setError() {
        synchronized(lock) { mIoError = true }
    }

    private fun initDefaultEncoder() {
        mCharset = Charset.defaultCharset().newEncoder()
        mCharset?.onMalformedInput(CodingErrorAction.REPLACE)
        mCharset?.onUnmappableCharacter(CodingErrorAction.REPLACE)
    }

    @Throws(IOException::class)
    private fun appendLocked(c: Char) {
        var pos = mPos
        if (pos >= mBufferLen - 1) {
            flushLocked()
            pos = mPos
        }
        mText[pos] = c
        mPos = pos + 1
    }

    @Throws(IOException::class)
    private fun appendLocked(str: String, i: Int, length: Int) {
        var int = i
        val buffLen = mBufferLen
        if (length > buffLen) {
            val end = int + length
            while (int < end) {
                val next = int + buffLen
                appendLocked(str, int, if (next < end) buffLen else end - int)
                int = next
            }
            return
        }
        var pos = mPos
        if (pos + length > buffLen) {
            flushLocked()
            pos = mPos
        }
        str.toCharArray(mText, pos, int, int + length)
        mPos = pos + length
    }

    @Throws(IOException::class)
    private fun appendLocked(buf: CharArray, i: Int, length: Int) {
        var int = i
        val buffLen = mBufferLen
        if (length > buffLen) {
            val end = int + length
            while (int < end) {
                val next = int + buffLen
                appendLocked(buf, int, if (next < end) buffLen else end - int)
                int = next
            }
            return
        }
        var pos = mPos
        if (pos + length > buffLen) {
            flushLocked()
            pos = mPos
        }
        System.arraycopy(buf, int, mText, pos, length)
        mPos = pos + length
    }

    @Throws(IOException::class)
    private fun flushBytesLocked() {
        if (mIoError.not()) {
            mBytes?.apply {
                var position: Int
                if (mBytes.position().also { position = it } > 0) {
                    mBytes.flip()
                    mOutputStream?.write(mBytes.array(), 0, position)
                    mBytes.clear()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun flushLocked() {
        //Log.i("PackageManager", "flush mPos=" + mPos);
        if (mPos > 0) {
            if (mOutputStream != null) {
                val charBuffer = CharBuffer.wrap(mText, 0, mPos)
                var result = mCharset?.encode(charBuffer, mBytes, true)
                while (mIoError.not()) {
                    if (result?.isError == true) {
                        throw IOException(result.toString())
                    } else if (result?.isOverflow == true) {
                        flushBytesLocked()
                        result = mCharset?.encode(charBuffer, mBytes, true)
                        continue
                    }
                    break
                }
                if (mIoError.not()) {
                    flushBytesLocked()
                    mOutputStream.flush()
                }
            }
            else if (mWriter != null) {
                if (mIoError.not()) {
                    mWriter.write(mText, 0, mPos)
                    mWriter.flush()
                }
            } else {
                var nonEolOff = 0
                val sepLen = mSeparator.length
                val len = if (sepLen < mPos) sepLen else mPos
                while (nonEolOff < len && mText[mPos - 1 - nonEolOff]
                    == mSeparator[mSeparator.length - 1 - nonEolOff]
                ) {
                    nonEolOff++
                }
                if (nonEolOff >= mPos) {
                    mPrinter?.println("")
                } else {
                    mPrinter?.println(String(mText, 0, mPos - nonEolOff))
                }
            }
            mPos = 0
        }
    }

    /**
     * Ensures that all pending data is sent out to the target. It also
     * flushes the target. If an I/O error occurs, this writer's error
     * state is set to `true`.
     */
    override fun flush() {
        synchronized(lock) {
            try {
                flushLocked()
                if (mIoError.not()) {
                    mOutputStream?.flush() ?: mWriter?.flush()
                }
            } catch (e: IOException) {
                kotlin.io.println("${TAG}. Write failure at flush(). ${e.message}")
                setError()
            }
        }
    }

    override fun close() {
        synchronized(lock) {
            try {
                flushLocked()
                mOutputStream?.close() ?: mWriter?.close()
            } catch (e: IOException) {
                kotlin.io.println("${TAG}. Write failure at close(). ${e.message}")
                setError()
            }
        }
    }

    /**
     * Prints the string representation of the specified character array
     * to the target.
     *
     * @param charArray
     * the character array to print to the target.
     * @see .print
     */
    override fun print(charArray: CharArray) {
        synchronized(lock) {
            try {
                appendLocked(charArray, 0, charArray.size)
            } catch (e: IOException) {
                kotlin.io.println("${TAG}. Write failure at print(charArray: CharArray). ${e.message}")
                setError()
            }
        }
    }

    /**
     * Prints the string representation of the specified character to the
     * target.
     *
     * @param ch
     * the character to print to the target.
     * @see .print
     */
    override fun print(ch: Char) {
        synchronized(lock) {
            try {
                appendLocked(ch)
            } catch (e: IOException) {
                kotlin.io.println("${TAG}. Write failure at print(ch: Char). ${e.message}")
                setError()
            }
        }
    }

    /**
     * Prints a string to the target. The string is converted to an array of
     * bytes using the encoding chosen during the construction of this writer.
     * The bytes are then written to the target with `write(int)`.
     *
     *
     * If an I/O error occurs, this writer's error flag is set to `true`.
     *
     * @param str
     * the string to print to the target.
     * @see .write
     */
    override fun print(str: String) {
        synchronized(lock) {
            try {
                appendLocked(str, 0, str.length)
            } catch (e: IOException) {
                kotlin.io.println("${TAG}. Write failure at print(str: String). ${e.message}")
                setError()
            }
        }
    }

    override fun print(inum: Int) {
        if (inum == 0) {
            print("0")
        } else {
            super.print(inum)
        }
    }

    override fun print(lnum: Long) {
        if (lnum == 0L) {
            print("0")
        } else {
            super.print(lnum)
        }
    }

    /**
     * Prints a newline. Flushes this writer if the autoFlush flag is set to `true`.
     */
    override fun println() {
        synchronized(lock) {
            try {
                appendLocked(mSeparator, 0, mSeparator.length)
                if (mAutoFlush) {
                    flushLocked()
                }
            } catch (e: IOException) {
                kotlin.io.println("${TAG}. Write failure at println(). ${e.message}")
                setError()
            }
        }
    }

    override fun println(inum: Int) {
        if (inum == 0) {
            println("0")
        } else {
            super.println(inum)
        }
    }

    override fun println(lnum: Long) {
        if (lnum == 0L) {
            println("0")
        } else {
            super.println(lnum)
        }
    }

    /**
     * Prints the string representation of the character array `chars` followed by a newline.
     * Flushes this writer if the autoFlush flag is set to `true`.
     */
    override fun println(chars: CharArray) {
        print(chars)
        println()
    }

    /**
     * Prints the string representation of the char `c` followed by a newline.
     * Flushes this writer if the autoFlush flag is set to `true`.
     */
    override fun println(c: Char) {
        print(c)
        println()
    }

    /**
     * Writes `count` characters from `buffer` starting at `offset` to the target.
     *
     *
     * This writer's error flag is set to `true` if this writer is closed
     * or an I/O error occurs.
     *
     * @param buf
     * the buffer to write to the target.
     * @param offset
     * the index of the first character in `buffer` to write.
     * @param count
     * the number of characters in `buffer` to write.
     * @throws IndexOutOfBoundsException
     * if `offset < 0` or `count < 0`, or if `offset + count` is greater than the length of `buf`.
     */
    override fun write(buf: CharArray, offset: Int, count: Int) {
        synchronized(lock) {
            try {
                appendLocked(buf, offset, count)
            } catch (e: IOException) {
                kotlin.io.println("${TAG}. Write failure at write(buf: CharArray, offset: Int, count: Int). ${e.message}")
                setError()
            }
        }
    }

    /**
     * Writes one character to the target. Only the two least significant bytes
     * of the integer `oneChar` are written.
     *
     *
     * This writer's error flag is set to `true` if this writer is closed
     * or an I/O error occurs.
     *
     * @param oneChar
     * the character to write to the target.
     */
    override fun write(oneChar: Int) {
        synchronized(lock) {
            try {
                appendLocked(oneChar.toChar())
            } catch (e: IOException) {
                kotlin.io.println("${TAG}. Write failure at write(oneChar: Int). ${e.message}")
                setError()
            }
        }
    }

    /**
     * Writes the characters from the specified string to the target.
     *
     * @param str
     * the non-null string containing the characters to write.
     */
    override fun write(str: String) {
        synchronized(lock) {
            try {
                appendLocked(str, 0, str.length)
            } catch (e: IOException) {
                kotlin.io.println("${TAG}. Write failure at write(str: String). ${e.message}")
                setError()
            }
        }
    }

    /**
     * Writes `count` characters from `str` starting at `offset` to the target.
     *
     * @param str
     * the non-null string containing the characters to write.
     * @param offset
     * the index of the first character in `str` to write.
     * @param count
     * the number of characters from `str` to write.
     * @throws IndexOutOfBoundsException
     * if `offset < 0` or `count < 0`, or if `offset + count` is greater than the length of `str`.
     */
    override fun write(str: String, offset: Int, count: Int) {
        synchronized(lock) {
            try {
                appendLocked(str, offset, count)
            } catch (e: IOException) {
                kotlin.io.println("${TAG}. Write failure at write(str: String, offset: Int, count: Int). ${e.message}")
                setError()
            }
        }
    }

    /**
     * Appends a subsequence of the character sequence `csq` to the
     * target. This method works the same way as `PrintWriter.print(csq.subsequence(start, end).toString())`. If `csq` is `null`, then the specified subsequence of the string "null"
     * will be written to the target.
     *
     * @param csq
     * the character sequence appended to the target.
     * @param start
     * the index of the first char in the character sequence appended
     * to the target.
     * @param end
     * the index of the character following the last character of the
     * subsequence appended to the target.
     * @return this writer.
     * @throws StringIndexOutOfBoundsException
     * if `start > end`, `start < 0`, `end < 0` or
     * either `start` or `end` are greater or equal than
     * the length of `csq`.
     */
    override fun append(csq: CharSequence, start: Int, end: Int): PrintWriter {
        val output = csq.subSequence(start, end).toString()
        write(output, 0, output.length)
        return this
    }

    companion object {
        const val TAG = "FastPrintWriter"
    }

}
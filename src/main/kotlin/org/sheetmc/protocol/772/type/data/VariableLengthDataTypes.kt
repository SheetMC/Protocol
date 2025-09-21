/*
 *     SheetMC Protocol: VariableLengthDataTypes.kt
 *     Copyright (C) 2025 SheetMC.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.sheetmc.protocol.`772`.type.data

import org.sheetmc.core.Outcome
import org.sheetmc.core.failure
import org.sheetmc.core.success
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

private const val SEGMENT_BITS = 0x7F
private const val CONTINUE_BIT = 0x80

fun OutputStream.writeVarInt(value: Int) {
    var v = value
    while (true) {
        if ((v and SEGMENT_BITS.inv()) == 0) {
            this.write(v)
            return
        }
        this.write((v and SEGMENT_BITS) or CONTINUE_BIT)
        v = v ushr 7
    }
}

fun OutputStream.writeVarLong(value: Long) {
    var v = value
    while (true) {
        if ((v and SEGMENT_BITS.toLong().inv()) == 0L) {
            this.write(v.toInt())
            return
        }
        this.write(((v and SEGMENT_BITS.toLong()) or CONTINUE_BIT.toLong()).toInt())
        v = v ushr 7
    }
}

fun InputStream.readVarInt(): Outcome<Int> {
    var numRead = 0
    var result = 0
    return try {
        while (true) {
            val read = read()
            if (read == -1) return failure("Stream ended unexpectedly")
            result = result or ((read and SEGMENT_BITS) shl (7 * numRead))
            numRead++
            if ((read and CONTINUE_BIT) == 0) return success(result)
            if (numRead > 5) return failure("VarInt is too big")
        }
        @Suppress("UNREACHABLE_CODE")
        success(result)
    } catch (e: IOException) {
        failure("I/O error while reading VarInt", e)
    }
}

fun InputStream.readVarLong(): Outcome<Long> {
    var numRead = 0
    var result = 0L
    return try {
        while (true) {
            val read = read()
            if (read == -1) return failure("Stream ended unexpectedly")
            result = result or ((read and SEGMENT_BITS).toLong() shl (7 * numRead))
            numRead++
            if ((read and CONTINUE_BIT) == 0) return success(result)
            if (numRead > 10) return failure("VarLong is too big")
        }
        @Suppress("UNREACHABLE_CODE")
        success(result)
    } catch (e: IOException) {
        failure("I/O error while reading VarLong", e)
    }
}
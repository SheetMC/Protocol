/*
 *     SheetMC Protocol: Array.kt
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
import java.io.InputStream
import java.io.OutputStream

fun <T> OutputStream.writePrefixedArray(values: List<T>, writer: (OutputStream, T) -> Unit) {
    writeVarInt(values.size)
    values.forEach { writer(this, it) }
}

fun <T> OutputStream.writeArray(values: List<T>, writer: (OutputStream, T) -> Unit) {
    values.forEach { value ->
        writer(this, value)
    }
}

fun <T> InputStream.readPrefixedArray(reader: (InputStream) -> T): Outcome<List<T>> {
    return try {
        when (val lengthOutcome = readVarInt()) {
            is Outcome.Success -> {
                val length = lengthOutcome.value
                if (length < 0) return failure("Array length is negative: $length")
                val list = ArrayList<T>(length.coerceAtLeast(0))
                repeat(length) { list += reader(this) }
                success(list)
            }

            is Outcome.Failure -> failure("Failed to read prefixed array length", lengthOutcome.throwable)
        }
    } catch (e: Exception) {
        failure("Failed to read prefixed array", e)
    }
}

fun <T> InputStream.readArray(length: Int, reader: (InputStream) -> T): Outcome<List<T>> {
    if (length < 0) return failure("Array length is negative: $length")
    return try {
        val list = ArrayList<T>(length)
        repeat(length) { list += reader(this) }
        success(list)
    } catch (e: Exception) {
        failure("Failed to read array of length $length", e)
    }
}
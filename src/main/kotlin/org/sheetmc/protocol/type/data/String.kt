/*
 *     SheetMC Protocol: String.kt
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

package org.sheetmc.protocol.type.data

import org.sheetmc.core.Outcome
import org.sheetmc.core.failure
import org.sheetmc.core.success
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

fun OutputStream.writeString(value: String) {
    val bytes = value.toByteArray(Charsets.UTF_8)
    writeVarInt(bytes.size)
    write(bytes)
}

fun InputStream.readString(): Outcome<String> {
    return try {
        when (val lengthOutcome = readVarInt()) {
            is Outcome.Success -> {
                val length = lengthOutcome.value
                if (length !in 0..32767) return failure("String length is invalid: $length")
                val bytes = ByteArray(length)
                val bytesRead = read(bytes)
                if (bytesRead != length) return failure("Stream ended unexpectedly while reading string of length $length")
                success(String(bytes, Charsets.UTF_8))
            }

            is Outcome.Failure -> failure("Failed to read string length VarInt", lengthOutcome.throwable)
        }
    } catch (e: IOException) {
        failure("I/O error while reading string", e)
    }
}
/*
 *     SheetMC Protocol: Other.kt
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
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun InputStream.readFully(b: ByteArray, off: Int = 0, len: Int = b.size) {
    var totalBytesRead = 0
    while (totalBytesRead < len) {
        val bytesRead = read(b, off + totalBytesRead, len - totalBytesRead)
        if (bytesRead == -1) {
            throw java.io.EOFException("End of stream reached before reading all bytes")
        }
        totalBytesRead += bytesRead
    }
}

fun OutputStream.writeFloat(value: Float) {
    val bytes = ByteArray(4)
    ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).putFloat(value)
    write(bytes)
}

fun InputStream.readFloat(): Outcome<Float> {
    val bytes = ByteArray(4)
    try {
        readFully(bytes)
    } catch (e: java.io.EOFException) {
        return failure("Stream ended unexpectedly while reading float")
    }
    return success(ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).float)
}
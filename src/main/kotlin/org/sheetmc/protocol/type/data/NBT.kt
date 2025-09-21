/*
 *     SheetMC Protocol: NBT.kt
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

import net.benwoodworth.knbt.*
import org.sheetmc.core.Outcome
import org.sheetmc.core.failure
import org.sheetmc.core.success
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

val DEFAULT_NBT = Nbt {
    variant = NbtVariant.Java
    compression = NbtCompression.None
}

inline fun <reified T> OutputStream.writeNBT(data: T, nbt: Nbt = DEFAULT_NBT) {
    nbt.encodeToStream(data, this)
}

inline fun <reified T> InputStream.readNBT(nbt: Nbt = DEFAULT_NBT): Outcome<T> {
    return try {
        success(nbt.decodeFromStream(this))
    } catch (e: IOException) {
        failure("I/O error while reading NBT", e)
    } catch (e: Exception) {
        failure("An unexpected error occurred while reading NBT", e)
    }
}
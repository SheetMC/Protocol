/*
 *     SheetMC Protocol: RegistryReferences.kt
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
import org.sheetmc.protocol.type.Identifier
import org.sheetmc.protocol.type.readIdentifier
import org.sheetmc.protocol.type.writeIdentifier
import java.io.InputStream
import java.io.OutputStream

fun <T> OutputStream.writeRegistryReference(value: T, writer: (OutputStream, T) -> Unit, registry: Map<T, Int>) {
    val id = registry[value]
    if (id != null) {
        writeVarInt(id + 1)
    } else {
        writeVarInt(0)
        writer(this, value)
    }
}

fun OutputStream.writeIdSet(tag: Identifier?, ids: List<Int>?) {
    when {
        tag != null -> {
            writeVarInt(0)
            writeIdentifier(tag)
        }

        ids != null -> {
            writeVarInt(ids.size + 1)
            writeArray(ids) { out, v -> out.writeVarInt(v) }
        }

        else -> throw IllegalArgumentException("Either tag or ids must be provided")
    }
}

fun <T> InputStream.readRegistryReference(reader: (InputStream) -> T, registry: Map<Int, T>): Outcome<T> =
    when (val flag = readVarInt()) {
        is Outcome.Success -> {
            val idFlag = flag.value
            if (idFlag == 0) success(reader(this))
            else registry[idFlag - 1]?.let { success(it) } ?: failure("Unknown registry ID: ${idFlag - 1}")
        }

        is Outcome.Failure -> flag
    }

fun InputStream.readIdSet(): Outcome<Pair<Identifier?, List<Int>?>> = try {
    when (val typeOutcome = readVarInt()) {
        is Outcome.Success -> {
            val type = typeOutcome.value
            if (type == 0) {
                when (val idOutcome = readIdentifier()) {
                    is Outcome.Success -> success(idOutcome.value to null)
                    is Outcome.Failure -> failure("Failed to read identifier for ID set.", idOutcome.throwable)
                }
            } else {
                val size = type - 1
                val ids = ArrayList<Int>(size)
                repeat(size) { i ->
                    when (val id = readVarInt()) {
                        is Outcome.Success -> ids += id.value
                        is Outcome.Failure -> return failure("Failed to read ID at index $i for ID set.", id.throwable)
                    }
                }
                success(null to ids)
            }
        }

        is Outcome.Failure -> failure("Failed to read type for ID set.", typeOutcome.throwable)
    }
} catch (e: Exception) {
    failure("An unexpected error occurred while reading the ID set", e)
}
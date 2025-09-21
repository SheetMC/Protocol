/*
 *     SheetMC Protocol: Identifier.kt
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

package org.sheetmc.protocol.`772`.type

import org.sheetmc.core.Outcome
import org.sheetmc.core.failure
import org.sheetmc.core.success
import org.sheetmc.protocol.`772`.type.data.readString
import org.sheetmc.protocol.`772`.type.data.writeString
import java.io.InputStream
import java.io.OutputStream

data class Identifier(val namespace: String, val value: String) {
    companion object {
        private val NAMESPACE_REGEX = Regex("^[a-z0-9._-]+$")
        private val VALUE_REGEX = Regex("^[a-z0-9._/-]+$")

        fun from(namespaceAndValue: String): Outcome<Identifier> {
            val split = namespaceAndValue.trim().split(":", limit = 2)
            if (split.size != 2) return failure("Namespace and value must be separated by a colon")
            return of(split[0], split[1])
        }

        fun of(namespace: String, value: String): Outcome<Identifier> {
            validate(namespace, NAMESPACE_REGEX, "Namespace")?.let { return it }
            validate(value, VALUE_REGEX, "Value")?.let { return it }
            return success(Identifier(namespace, value))
        }

        private fun validate(str: String, regex: Regex, name: String): Outcome.Failure? {
            if (str.isEmpty()) return failure("$name cannot be empty")
            if (!regex.matches(str)) return failure("$name must match the regex $regex")
            return null
        }
    }

    override fun toString(): String = "$namespace:$value"
}

fun OutputStream.writeIdentifier(identifier: Identifier) = writeString(identifier.toString())

fun InputStream.readIdentifier(): Outcome<Identifier> = when (val stringOutcome = readString()) {
    is Outcome.Success -> Identifier.from(stringOutcome.value)
    is Outcome.Failure -> stringOutcome
}
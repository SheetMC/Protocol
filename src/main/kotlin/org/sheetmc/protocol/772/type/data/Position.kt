/*
 *     SheetMC Protocol: Position.kt
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

class Position(var x: Long, var y: Long, var z: Long) {

    companion object {
        fun decode(encoded: Long): Position {
            val x = encoded shr 38
            val y = encoded shl 52 shr 52
            val z = encoded shl 26 shr 38

            return Position(x, y, z)
        }
    }

    fun encode(): Long =
        ((x and 0x3FFFFFF) shl 38) or ((z and 0x3FFFFFF) shl 12) or (y and 0xFFFL)
}
/*
 *     SheetMC Protocol: LegacyPosition.kt
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

package org.sheetmc.protocol.type.data.legacy

class LegacyPosition(var x: ULong, var y: ULong, var z: ULong) {

    companion object {
        fun decode(encoded: ULong): LegacyPosition {
            val x = encoded shr 38
            val y = (encoded shr 26) and 0xFFFu
            val z = encoded shl 38 shr 38
            return LegacyPosition(x, y, z)
        }
    }

    fun encode(): ULong =
        ((x and 0x3FFFFFFu) shl 38) or ((y and 0xFFFu) shl 26) or (z and 0x3FFFFFFu)
}
/*
 *     SheetMC Protocol: RegistryData.kt
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
import org.sheetmc.protocol.`772`.type.Identifier
import org.sheetmc.protocol.`772`.type.readIdentifier
import org.sheetmc.protocol.`772`.type.writeIdentifier
import java.io.InputStream
import java.io.OutputStream

data class SoundEvent(
    val soundName: Identifier,
    val hasFixedRange: Boolean = false,
    val fixedRange: Float = 0f,
)

fun OutputStream.writeSoundEvent(soundEvent: SoundEvent) {
    writeIdentifier(soundEvent.soundName)
    write(if (soundEvent.hasFixedRange) 1 else 0)
    if (soundEvent.hasFixedRange) writeFloat(soundEvent.fixedRange)
}

fun InputStream.readSoundEvent(): Outcome<SoundEvent> {
    val identifierOutcome = readIdentifier()
    if (identifierOutcome is Outcome.Failure) return identifierOutcome
    val readByte = read()
    if (readByte == -1) return failure("Stream ended unexpectedly while reading sound event")
    val hasFixedRange = readByte == 1
    val fixedRangeOutcome: Outcome<Float> = if (hasFixedRange) readFloat() else success(0f)
    if (fixedRangeOutcome is Outcome.Failure) return fixedRangeOutcome

    val identifier = (identifierOutcome as Outcome.Success).value
    val fixedRange = (fixedRangeOutcome as Outcome.Success).value
    return success(SoundEvent(identifier, hasFixedRange, fixedRange))
}

sealed interface Chat {
    data class Decoration<T>(
        val translationKey: String,
        val parameters: List<Int>,
        val style: T?,
    )

    data class Type<T>(
        val chat: Decoration<T>,
        val narration: Decoration<T>,
    )
}

// TODO: Rework methods so that they are not inlined
inline fun <reified T> OutputStream.writeChatDecoration(chatDecoration: Chat.Decoration<T>) {
    writeString(chatDecoration.translationKey)
    writePrefixedArray(chatDecoration.parameters) { out, v -> out.writeVarInt(v) }
    writeNBT(chatDecoration.style)
}

inline fun <reified T> OutputStream.writeChatType(chatType: Chat.Type<T>) {
    writeChatDecoration(chatType.chat)
    writeChatDecoration(chatType.narration)
}

inline fun <reified T> InputStream.readChatDecoration(): Outcome<Chat.Decoration<T>> {
    val translationKeyOutcome = readString()
    if (translationKeyOutcome is Outcome.Failure) return translationKeyOutcome

    val parameters = mutableListOf<Int>()

    val sizeOutcome = readVarInt()
    if (sizeOutcome is Outcome.Failure) return sizeOutcome

    val size = (sizeOutcome as Outcome.Success).value
    for (i in 0 until size) {
        val varIntOutcome = readVarInt()
        if (varIntOutcome is Outcome.Failure) return varIntOutcome
        parameters.add((varIntOutcome as Outcome.Success).value)
    }

    val styleOutcome = readNBT<T>()
    if (styleOutcome is Outcome.Failure) return styleOutcome

    val translationKey = (translationKeyOutcome as Outcome.Success).value
    val style = (styleOutcome as Outcome.Success).value

    return success(Chat.Decoration(translationKey, parameters, style))
}

inline fun <reified T> InputStream.readChatType(): Outcome<Chat.Type<T>> {
    val chatDecorationOutcome = readChatDecoration<T>()
    if (chatDecorationOutcome is Outcome.Failure) return chatDecorationOutcome

    val narrationDecorationOutcome = readChatDecoration<T>()
    if (narrationDecorationOutcome is Outcome.Failure) return narrationDecorationOutcome

    val chatDecoration = (chatDecorationOutcome as Outcome.Success).value
    val narrationDecoration = (narrationDecorationOutcome as Outcome.Success).value

    return success(Chat.Type(chatDecoration, narrationDecoration))
}
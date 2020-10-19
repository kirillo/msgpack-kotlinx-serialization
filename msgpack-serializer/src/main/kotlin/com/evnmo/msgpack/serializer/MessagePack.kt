package com.evnmo.msgpack.serializer

import com.evnmo.msgpack.serializer.decoder.MessagePackDecoder
import com.evnmo.msgpack.serializer.encoder.MessagePackEncoder
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule

@ExperimentalSerializationApi
public open class MessagePack(public val configuration: MessagePackConf) : BinaryFormat {

    override val serializersModule: SerializersModule
        get() = configuration.serializersModule

    public companion object Default : MessagePack(MessagePackConf())

    override fun <T> decodeFromByteArray(
        deserializer: DeserializationStrategy<T>,
        bytes: ByteArray
    ): T {
        val unpacker = org.msgpack.core.MessagePack.newDefaultUnpacker(bytes)
        val decoder = MessagePackDecoder(unpacker, configuration)
        val result = decoder.decodeSerializableValue(deserializer)
        unpacker.close()

        return result
    }

    override fun <T> encodeToByteArray(
        serializer: SerializationStrategy<T>,
        value: T
    ): ByteArray {
        val packer = org.msgpack.core.MessagePack.newDefaultBufferPacker()
        val encoder = MessagePackEncoder(packer, configuration)
        encoder.encodeSerializableValue(serializer, value)
        packer.close()

        return packer.toByteArray()
    }
}
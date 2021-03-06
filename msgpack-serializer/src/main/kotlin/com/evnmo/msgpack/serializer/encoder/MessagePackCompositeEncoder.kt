package com.evnmo.msgpack.serializer.encoder

import com.evnmo.msgpack.serializer.DateSerializer
import com.evnmo.msgpack.serializer.Logger
import com.evnmo.msgpack.serializer.MessagePackConf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import org.msgpack.core.MessageBufferPacker
import java.nio.ByteBuffer
import java.util.*

@ExperimentalSerializationApi
internal class MessagePackCompositeEncoder(
    private val packer: MessageBufferPacker,
    private val configuration: MessagePackConf
) : CompositeEncoder {

    private val logger = Logger(configuration)

    override val serializersModule = configuration.serializersModule

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        logger.log("encodeBooleanElement: $value")
        packer.packBoolean(value)
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        logger.log("encodeByteElement: $value")
        packer.packByte(value)
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        logger.log("encodeCharElement: $value")
        packer.packString(value.toString())
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        logger.log("encodeDoubleElement: $value")
        packer.packDouble(value)
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        logger.log("encodeFloatElement: $value")
        packer.packFloat(value)
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        logger.log("encodeIntElement: $value")
        packer.packInt(value)
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        logger.log("encodeLongElement: $value")
        packer.packLong(value)
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        logger.log("encodeShortElement: $value")
        packer.packShort(value)
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        logger.log("encodeStringElement: $value")
        packer.packString(value)
    }

    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        if (value == null) {
            logger.log("encodeNullableSerializableElement: null")
            packer.packNil()
        } else {
            encodeSerializableElement(descriptor, index, serializer, value)
        }
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        if (descriptor.serialName == DateSerializer.descriptor.serialName) {
            logger.log("encodeDate: $value")
            val unixSeconds = (value as Date).time
            val byteArray =
                ByteBuffer.allocate(DateSerializer.DATE_LENGTH).putInt((unixSeconds / 1000).toInt())
                    .array()
            packer.writePayload(byteArray)
        } else {
            serializer.serialize(MessagePackEncoder(packer, configuration), value)
        }
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        logger.log("endStructure")
    }
}
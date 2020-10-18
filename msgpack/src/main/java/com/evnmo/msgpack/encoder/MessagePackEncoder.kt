package com.evnmo.msgpack.encoder

import com.evnmo.msgpack.Logger
import com.evnmo.msgpack.MessagePackConf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import org.msgpack.core.MessageBufferPacker

@ExperimentalSerializationApi
internal class MessagePackEncoder(
    private val packer: MessageBufferPacker,
    private val configuration: MessagePackConf
) : Encoder {

    private val logger = Logger(configuration)

    override val serializersModule = configuration.serializersModule

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        logger.log("-- encodeSerializableValue --")
        super.encodeSerializableValue(serializer, value)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        logger.log("beginStructure")
        if (descriptor.kind == StructureKind.CLASS) {
            packer.packArrayHeader(descriptor.elementsCount)
        }
        return MessagePackCompositeEncoder(packer, configuration)
    }

    override fun encodeBoolean(value: Boolean) {
        logger.log("encodeBoolean: $value")
        packer.packBoolean(value)
    }

    override fun encodeByte(value: Byte) {
        logger.log("encodeByte: $value")
        packer.packByte(value)
    }

    override fun encodeChar(value: Char) {
        logger.log("encodeChar: $value")
        packer.packString(value.toString())
    }

    override fun encodeDouble(value: Double) {
        logger.log("encodeDouble: $value")
        packer.packDouble(value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        if (configuration.encodeEnumsAsStrings) {
            logger.log("encodeEnum: ${enumDescriptor.getElementName(index)}")
            packer.packString(enumDescriptor.getElementName(index))
        } else {
            logger.log("encodeEnum: $index")
            packer.packInt(index)
        }
    }

    override fun encodeFloat(value: Float) {
        logger.log("encodeFloat: $value")
        packer.packFloat(value)
    }

    override fun encodeInt(value: Int) {
        logger.log("encodeInt: $value")
        packer.packInt(value)
    }

    override fun encodeLong(value: Long) {
        logger.log("encodeLong: $value")
        packer.packLong(value)
    }

    override fun encodeNull() {
        logger.log("encodeNull")
        packer.packNil()
    }

    override fun encodeShort(value: Short) {
        logger.log("encodeShort: $value")
        packer.packShort(value)
    }

    override fun encodeString(value: String) {
        logger.log("encodeString: $value")
        packer.packString(value)
    }
}
package com.evnmo.msgpack.serializer.encoder

import com.evnmo.msgpack.serializer.DateSerializer
import com.evnmo.msgpack.serializer.Logger
import com.evnmo.msgpack.serializer.MessagePackConf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
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

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (descriptor.kind == StructureKind.CLASS) {
            if (descriptor.serialName == DateSerializer.descriptor.serialName) {
                logger.log("beginStructure: date")
                packer.packExtensionTypeHeader(-1, DateSerializer.DATE_LENGTH)
            } else {
                logger.log("beginStructure: class")
                packer.packArrayHeader(descriptor.elementsCount)
            }
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
            val enumName = enumDescriptor.getElementName(index)
            logger.log("encodeEnum (string): $enumName")
            packer.packString(enumName)
        } else {
            logger.log("encodeEnum (index): $index")
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

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int
    ): CompositeEncoder {
        when (descriptor.kind) {
            is StructureKind.LIST -> {
                if (descriptor.getElementDescriptor(0).kind == PrimitiveKind.BYTE) {
                    logger.log("beginCollection: binary, size: $collectionSize")
                    packer.packBinaryHeader(collectionSize)
                } else {
                    logger.log("beginCollection: list, size: $collectionSize")
                    packer.packArrayHeader(collectionSize)
                }
            }
            is StructureKind.MAP -> {
                logger.log("beginCollection: map, size: $collectionSize")
                packer.packMapHeader(collectionSize)
            }
            else -> logger.log("UNEXPECTED COLLECTION TYPE: ${descriptor.kind}")

        }
        return MessagePackCompositeEncoder(packer, configuration)
    }
}
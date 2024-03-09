package ru.hse.server.configuration;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;

import jakarta.annotation.Nonnull;

import org.springframework.core.convert.converter.Converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonToProtobufConverter implements Converter<String, Message> {

    Logger logger = LoggerFactory.getLogger(JsonToProtobufConverter.class);

    @Override
    public Message convert(@Nonnull String json) {
        try {
            Message.Builder structBuilder = Struct.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(json, structBuilder);
            return structBuilder.build();
        } catch (InvalidProtocolBufferException e) {
            logger.error("can not convert json to protobuf since invalid json format", e);
            return null;
        }
    }
}

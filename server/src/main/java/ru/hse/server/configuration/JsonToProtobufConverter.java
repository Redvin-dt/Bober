package ru.hse.server.configuration;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import org.springframework.core.convert.converter.Converter;

public class JsonToProtobufConverter implements Converter<String, Message> {
    @Override
    public Message convert(String json) {
        try {
            Message.Builder structBuilder = Struct.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(json, structBuilder);
            return structBuilder.build();
        } catch (InvalidProtocolBufferException e) {
            // TODO: add some log
            return null;
        }
    }
}

package io.littlehorse.infra;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import io.quarkus.jackson.ObjectMapperCustomizer;

import jakarta.inject.Singleton;

import java.io.IOException;

@Singleton
public class ProtobufObjectMapperCustomizer implements ObjectMapperCustomizer {

    public void customize(ObjectMapper mapper) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Message.class, new ProtobufMessageSerializer());
        mapper.registerModule(simpleModule);
    }

    public static class ProtobufMessageSerializer extends JsonSerializer<Message> {

        @Override
        public void serialize(Message value, JsonGenerator generator, SerializerProvider provider)
                throws IOException {
            generator.writeRaw(
                    JsonFormat.printer().alwaysPrintFieldsWithNoPresence().print(value));
        }
    }
}

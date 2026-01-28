package io.littlehorse.quarkus.rest.gateway.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.Message;

import io.quarkus.jackson.ObjectMapperCustomizer;

import jakarta.inject.Singleton;

@Singleton
public class ProtobufObjectMapperCustomizer implements ObjectMapperCustomizer {

    public void customize(ObjectMapper mapper) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Message.class, new ProtobufMessageSerializer());
        mapper.registerModule(simpleModule);
    }
}

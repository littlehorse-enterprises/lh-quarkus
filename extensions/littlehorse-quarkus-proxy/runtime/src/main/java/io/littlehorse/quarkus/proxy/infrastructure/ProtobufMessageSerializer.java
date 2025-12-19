package io.littlehorse.quarkus.proxy.infrastructure;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;

public class ProtobufMessageSerializer extends JsonSerializer<Message> {

    @Override
    public void serialize(Message value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeRaw(
                JsonFormat.printer().alwaysPrintFieldsWithNoPresence().print(value));
    }
}

package io.littlehorse.quarkus.proxy.protobuf;

import com.google.protobuf.ByteString;

import java.util.Base64;

public final class ByteStringUtils {

    private ByteStringUtils() {}

    public static String byteStringToString(ByteString byteString) {
        return Base64.getEncoder().encodeToString(byteString.toByteArray());
    }

    public static ByteString stringToByteString(String string) {
        return ByteString.copyFrom(Base64.getDecoder().decode(string));
    }
}

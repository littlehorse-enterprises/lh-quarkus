package io.littlehorse.quarkus.proxy.protobuf;

import com.google.protobuf.Timestamp;

import java.time.Instant;

public final class TimestampUtils {
    private TimestampUtils() {}

    public static Instant timestampToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}

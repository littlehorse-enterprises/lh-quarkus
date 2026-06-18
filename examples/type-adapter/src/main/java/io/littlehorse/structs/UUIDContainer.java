package io.littlehorse.structs;

import io.littlehorse.sdk.worker.LHStructDef;

import java.util.UUID;

@LHStructDef("uuid-container")
public class UUIDContainer {

    private UUID uuid;
    private String message;

    public UUIDContainer() {}

    public UUIDContainer(UUID uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UUIDContainer{uuid=%s, message='%s'}".formatted(uuid, message);
    }
}

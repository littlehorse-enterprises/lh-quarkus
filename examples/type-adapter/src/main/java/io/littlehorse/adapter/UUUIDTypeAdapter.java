package io.littlehorse.adapter;

import io.littlehorse.sdk.worker.adapter.LHStringAdapter;

import jakarta.inject.Singleton;

import java.util.UUID;

@Singleton
public class UUUIDTypeAdapter implements LHStringAdapter<UUID> {

    @Override
    public String toString(UUID src) {
        return src.toString();
    }

    @Override
    public UUID fromString(String src) {
        return UUID.fromString(src);
    }

    @Override
    public Class<UUID> getTypeClass() {
        return UUID.class;
    }
}

package io.littlehorse.adapter;

import io.littlehorse.quarkus.task.LHTypeAdapter;
import io.littlehorse.sdk.common.adapter.LHStringAdapter;
import io.littlehorse.sdk.common.proto.VariableType;

import java.util.UUID;

@LHTypeAdapter(variableType = VariableType.STR, adaptedType = UUID.class)
public class UUIDTypeAdapter implements LHStringAdapter<UUID> {

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

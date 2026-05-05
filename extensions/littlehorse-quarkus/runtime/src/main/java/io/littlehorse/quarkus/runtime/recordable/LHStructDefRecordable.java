package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.sdk.common.proto.StructDefId;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructDefType;
import io.quarkus.runtime.annotations.RecordableConstructor;

import java.util.Set;
import java.util.stream.Collectors;

public class LHStructDefRecordable extends LHRecordable {

    private final String description;

    @RecordableConstructor
    public LHStructDefRecordable(Class<?> beanClass, String name, String description) {
        super(beanClass, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public Set<String> dependencies() {
        LHStructDefType structDefType = new LHStructDefType(getBeanClass());
        return structDefType.getDependencyClasses().stream()
                .map(LHStructDefType::getStructDefId)
                .map(StructDefId::getName)
                .filter(name -> !name.equals(getName()))
                .collect(Collectors.toSet());
    }
}

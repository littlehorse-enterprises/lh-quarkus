package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructDefType;
import io.littlehorse.sdk.worker.LHStructDef;
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
        LHConfig config = getBean(LHConfig.class);
        LHStructDefType structDefType = new LHStructDefType(
                getBeanClass(), config.getTypeAdapterRegistry(), getPlaceholderValues());
        return structDefType.getDependencyClasses().stream()
                .map(dependency -> dependency
                        .getClassType()
                        .getAnnotation(LHStructDef.class)
                        .value())
                .filter(name -> !name.equals(getName()))
                .collect(Collectors.toSet());
    }
}

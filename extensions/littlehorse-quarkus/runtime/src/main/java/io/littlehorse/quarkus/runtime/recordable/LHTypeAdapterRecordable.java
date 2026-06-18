package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.sdk.common.proto.VariableType;
import io.quarkus.runtime.annotations.RecordableConstructor;

public class LHTypeAdapterRecordable {

    private final Class<?> beanClass;
    private final Class<?> adaptedType;
    private final VariableType variableType;

    @RecordableConstructor
    public LHTypeAdapterRecordable(
            Class<?> beanClass, Class<?> adaptedType, VariableType variableType) {
        this.beanClass = beanClass;
        this.adaptedType = adaptedType;
        this.variableType = variableType;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Class<?> getAdaptedType() {
        return adaptedType;
    }

    public VariableType getVariableType() {
        return variableType;
    }
}

package io.littlehorse.quarkus.runtime.recordable;

import io.quarkus.runtime.annotations.RecordableConstructor;

public class LHTaskMethodRecordable extends LHRecordable {

    private final String description;

    @RecordableConstructor
    public LHTaskMethodRecordable(Class<?> beanClass, String name, String description) {
        super(beanClass, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

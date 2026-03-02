package io.littlehorse.quarkus.runtime.recordable;

import io.quarkus.runtime.annotations.RecordableConstructor;

public class LHUserTaskFormRecordable extends LHRecordable {

    @RecordableConstructor
    public LHUserTaskFormRecordable(Class<?> beanClass, String name) {
        super(beanClass, name);
    }
}

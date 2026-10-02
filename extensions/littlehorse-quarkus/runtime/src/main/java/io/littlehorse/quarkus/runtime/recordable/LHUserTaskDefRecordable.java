package io.littlehorse.quarkus.runtime.recordable;

import io.quarkus.runtime.annotations.RecordableConstructor;

public class LHUserTaskDefRecordable extends LHRecordable {

    private final String resultStructDefName;

    public String getResultStructDefName() {
        return resultStructDefName;
    }

    @RecordableConstructor
    public LHUserTaskDefRecordable(Class<?> beanClass, String name, String resultStructDefName) {
        super(beanClass, name);
        this.resultStructDefName = resultStructDefName;
    }
}

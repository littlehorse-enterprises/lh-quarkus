package io.littlehorse.quarkus.runtime.recordable;

import io.quarkus.runtime.annotations.RecordableConstructor;

import java.util.List;

public class LHTaskMethodRecordable extends LHRecordable {

    private final String description;
    private final List<String> structDefNameExpressions;

    public LHTaskMethodRecordable(Class<?> beanClass, String name, String description) {
        this(beanClass, name, description, List.of());
    }

    @RecordableConstructor
    public LHTaskMethodRecordable(
            Class<?> beanClass,
            String name,
            String description,
            List<String> structDefNameExpressions) {
        super(beanClass, name);
        this.description = description;
        this.structDefNameExpressions = List.copyOf(structDefNameExpressions);
    }

    public String getDescription() {
        return description;
    }

    public List<String> getStructDefNameExpressions() {
        return structDefNameExpressions;
    }
}

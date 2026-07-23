package io.littlehorse.quarkus.runtime.recordable;

import io.quarkus.runtime.annotations.RecordableConstructor;

import java.util.List;

public class LHTaskMethodRecordable extends LHRecordable {

    private final String description;
    private final List<String> structDefNameTemplates;

    public LHTaskMethodRecordable(Class<?> beanClass, String name, String description) {
        this(beanClass, name, description, List.of());
    }

    @RecordableConstructor
    public LHTaskMethodRecordable(
            Class<?> beanClass,
            String name,
            String description,
            List<String> structDefNameTemplates) {
        super(beanClass, name);
        this.description = description;
        this.structDefNameTemplates = List.copyOf(structDefNameTemplates);
    }

    public String getDescription() {
        return description;
    }

    public List<String> getStructDefNameTemplates() {
        return structDefNameTemplates;
    }
}

package io.littlehorse.quarkus.runtime.recordable;

import io.quarkus.runtime.annotations.RecordableConstructor;

import java.util.Set;

public class LHTaskMethodRecordable extends LHRecordable {

    private final String description;
    private final Set<String> structDefNameTemplates;

    public LHTaskMethodRecordable(Class<?> beanClass, String name, String description) {
        this(beanClass, name, description, Set.of());
    }

    @RecordableConstructor
    public LHTaskMethodRecordable(
            Class<?> beanClass,
            String name,
            String description,
            Set<String> structDefNameTemplates) {
        super(beanClass, name);
        this.description = description;
        this.structDefNameTemplates = Set.copyOf(structDefNameTemplates);
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getStructDefNameTemplates() {
        return structDefNameTemplates;
    }
}

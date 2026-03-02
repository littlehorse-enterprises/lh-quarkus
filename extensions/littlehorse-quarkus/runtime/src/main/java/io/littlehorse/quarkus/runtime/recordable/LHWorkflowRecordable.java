package io.littlehorse.quarkus.runtime.recordable;

import io.quarkus.runtime.annotations.RecordableConstructor;

import java.util.List;

public class LHWorkflowRecordable extends LHRecordable {

    private final String beanMethodName;
    private final String parent;
    private final String defaultTaskTimeout;
    private final String defaultTaskRetries;
    private final String updateType;
    private final String retention;
    private final String defaultThreadRetention;
    private final LHExponentialBackoffRetryRecordable retryRecordable;

    @RecordableConstructor
    public LHWorkflowRecordable(
            Class<?> beanClass,
            String name,
            String beanMethodName,
            String parent,
            String defaultTaskTimeout,
            String defaultTaskRetries,
            String updateType,
            String retention,
            String defaultThreadRetention,
            LHExponentialBackoffRetryRecordable retryRecordable) {
        super(beanClass, name);
        this.beanMethodName = beanMethodName;
        this.parent = parent;
        this.defaultTaskTimeout = defaultTaskTimeout;
        this.defaultTaskRetries = defaultTaskRetries;
        this.updateType = updateType;
        this.retention = retention;
        this.defaultThreadRetention = defaultThreadRetention;
        this.retryRecordable = retryRecordable;
    }

    public String getBeanMethodName() {
        return beanMethodName;
    }

    public String getDefaultTaskRetries() {
        return defaultTaskRetries;
    }

    public String getDefaultTaskTimeout() {
        return defaultTaskTimeout;
    }

    public String getParent() {
        return parent;
    }

    public String getUpdateType() {
        return updateType;
    }

    public String getRetention() {
        return retention;
    }

    public String getDefaultThreadRetention() {
        return defaultThreadRetention;
    }

    public LHExponentialBackoffRetryRecordable getRetryRecordable() {
        return retryRecordable;
    }

    @Override
    public List<String> dependencies() {
        if (getParent() == null) {
            return List.of();
        }
        return List.of(getParent());
    }
}

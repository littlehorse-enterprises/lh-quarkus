package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.config.ConfigExpression;
import io.littlehorse.quarkus.runtime.register.LHWorkflowRegister;
import io.littlehorse.sdk.common.proto.AllowedUpdateType;
import io.littlehorse.sdk.common.proto.ExponentialBackoffRetryPolicy;
import io.littlehorse.sdk.common.proto.ThreadRetentionPolicy;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

import jakarta.enterprise.inject.spi.CDI;

public abstract class LHWorkflowRecordable extends LHRecordable {

    private final String parent;
    private final String defaultTaskTimeout;
    private final String defaultTaskRetries;
    private final String updateType;
    private final String retention;
    private final String defaultThreadRetention;
    private final LHExponentialBackoffRetryRecordable retryRecordable;

    public LHWorkflowRecordable(
            Class<?> beanClass,
            String name,
            String parent,
            String defaultTaskTimeout,
            String defaultTaskRetries,
            String updateType,
            String retention,
            String defaultThreadRetention,
            LHExponentialBackoffRetryRecordable retryRecordable) {
        super(beanClass, name);
        this.parent = parent;
        this.defaultTaskTimeout = defaultTaskTimeout;
        this.defaultTaskRetries = defaultTaskRetries;
        this.updateType = updateType;
        this.retention = retention;
        this.defaultThreadRetention = defaultThreadRetention;
        this.retryRecordable = retryRecordable;
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

    public abstract void buildWorkflowThread(WorkflowThread workflowThread);

    public void registerWorkflow() {
        if (!exists()) return;

        LHWorkflowRegister register =
                CDI.current().select(LHWorkflowRegister.class).get();

        Workflow workflow = Workflow.newWorkflow(
                ConfigExpression.expand(getName()).asString(), this::buildWorkflowThread);

        if (parent != null) {
            workflow.setParent(ConfigExpression.expand(parent).asString());
        }

        if (defaultTaskTimeout != null) {
            workflow.setDefaultTaskTimeout(
                    ConfigExpression.expand(defaultTaskTimeout).asInt());
        }

        if (defaultTaskRetries != null) {
            workflow.setDefaultTaskRetries(
                    ConfigExpression.expand(defaultTaskRetries).asInt());
        }

        if (updateType != null) {
            workflow.withUpdateType(AllowedUpdateType.valueOf(
                    ConfigExpression.expand(defaultTaskRetries).asString().toUpperCase()));
        }

        if (retention != null) {
            workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                    .setSecondsAfterWfTermination(
                            ConfigExpression.expand(retention).asLong())
                    .build());
        }

        if (defaultThreadRetention != null) {
            workflow.withDefaultThreadRetentionPolicy(ThreadRetentionPolicy.newBuilder()
                    .setSecondsAfterThreadTermination(
                            ConfigExpression.expand(defaultThreadRetention).asLong())
                    .build());
        }

        if (retryRecordable != null) {
            ExponentialBackoffRetryPolicy.Builder backoffRetryBuilder =
                    ExponentialBackoffRetryPolicy.newBuilder();

            if (retryRecordable.getBaseIntervalMs() != null) {
                backoffRetryBuilder.setBaseIntervalMs(
                        ConfigExpression.expand(retryRecordable.getBaseIntervalMs())
                                .asInt());
            }

            if (retryRecordable.getMultiplier() != null) {
                backoffRetryBuilder.setMultiplier(
                        ConfigExpression.expand(retryRecordable.getMultiplier()).asFloat());
            }

            if (retryRecordable.getMaxDelayMs() != null) {
                backoffRetryBuilder.setMaxDelayMs(
                        ConfigExpression.expand(retryRecordable.getMaxDelayMs()).asLong());
            }

            workflow.setDefaultTaskExponentialBackoffPolicy(backoffRetryBuilder.build());
        }

        register.registerWorkflow(workflow);
    }
}

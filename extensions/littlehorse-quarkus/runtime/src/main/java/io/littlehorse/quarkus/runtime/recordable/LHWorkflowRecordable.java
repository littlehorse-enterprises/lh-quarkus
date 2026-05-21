package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.AllowedUpdateType;
import io.littlehorse.sdk.common.proto.ExponentialBackoffRetryPolicy;
import io.littlehorse.sdk.common.proto.ThreadRetentionPolicy;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

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
    public Set<String> dependencies() {
        Workflow workflow = toWorkflow();
        Set<String> deps = new HashSet<>();
        if (getParent() != null) {
            deps.add(getParent());
        }
        deps.addAll(workflow.getRequiredChildWfSpecNames());
        return deps;
    }

    public Workflow toWorkflow() {
        LHConfig config = CDI.current().select(LHConfig.class).get();
        ConfigEvaluator configEvaluator =
                CDI.current().select(ConfigEvaluator.class).get();
        String expandedName = configEvaluator.expand(getName()).asString();

        Workflow workflow = Workflow.newWorkflow(expandedName, thread -> {
            if (getBeanMethodName() == null) {
                LHWorkflowDefinition workflowDefinitionBean = (LHWorkflowDefinition)
                        CDI.current().select(getBeanClass()).get();
                workflowDefinitionBean.define(thread);
                return;
            }

            try {
                Method method = getBeanClass().getMethod(getBeanMethodName(), WorkflowThread.class);
                method.invoke(CDI.current().select(getBeanClass()).get(), thread);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        String parent = getParent();
        if (parent != null) {
            workflow.setParent(configEvaluator.expand(parent).asString());
        }

        String defaultTaskTimeout = getDefaultTaskTimeout();
        if (defaultTaskTimeout != null) {
            workflow.setDefaultTaskTimeout(
                    configEvaluator.expand(defaultTaskTimeout).asInt());
        }

        String defaultTaskRetries = getDefaultTaskRetries();
        if (defaultTaskRetries != null) {
            workflow.setDefaultTaskRetries(
                    configEvaluator.expand(defaultTaskRetries).asInt());
        }

        String updateType = getUpdateType();
        if (updateType != null) {
            workflow.withUpdateType(AllowedUpdateType.valueOf(
                    configEvaluator.expand(updateType).asString().toUpperCase()));
        }

        String retention = getRetention();
        if (retention != null) {
            workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                    .setSecondsAfterWfTermination(
                            configEvaluator.expand(retention).asLong())
                    .build());
        }

        String defaultThreadRetention = getDefaultThreadRetention();
        if (defaultThreadRetention != null) {
            workflow.withDefaultThreadRetentionPolicy(ThreadRetentionPolicy.newBuilder()
                    .setSecondsAfterThreadTermination(
                            configEvaluator.expand(defaultThreadRetention).asLong())
                    .build());
        }

        LHExponentialBackoffRetryRecordable retryRecordable = getRetryRecordable();
        if (retryRecordable != null) {
            ExponentialBackoffRetryPolicy.Builder backoffRetryBuilder =
                    ExponentialBackoffRetryPolicy.newBuilder();

            if (retryRecordable.getBaseIntervalMs() != null) {
                backoffRetryBuilder.setBaseIntervalMs(configEvaluator
                        .expand(retryRecordable.getBaseIntervalMs())
                        .asInt());
            }

            if (retryRecordable.getMultiplier() != null) {
                backoffRetryBuilder.setMultiplier(
                        configEvaluator.expand(retryRecordable.getMultiplier()).asFloat());
            }

            if (retryRecordable.getMaxDelayMs() != null) {
                backoffRetryBuilder.setMaxDelayMs(
                        configEvaluator.expand(retryRecordable.getMaxDelayMs()).asLong());
            }

            workflow.setDefaultTaskExponentialBackoffPolicy(backoffRetryBuilder.build());
        }

        workflow.compileWorkflow(config);

        return workflow;
    }
}

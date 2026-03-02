package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.runtime.recordable.LHExponentialBackoffRetryRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHStructDefRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHUserTaskFormRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowRecordable;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.common.proto.AllowedUpdateType;
import io.littlehorse.sdk.common.proto.ExponentialBackoffRetryPolicy;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutStructDefRequest;
import io.littlehorse.sdk.common.proto.PutUserTaskDefRequest;
import io.littlehorse.sdk.common.proto.StructDefCompatibilityType;
import io.littlehorse.sdk.common.proto.ThreadRetentionPolicy;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.usertask.UserTaskSchema;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructDefType;
import io.littlehorse.sdk.worker.LHStructDef;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

import jakarta.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

@Recorder
public class LHRecorder {
    private static final Logger log = LoggerFactory.getLogger(LHRecorder.class);
    private final RuntimeValue<LHRuntimeConfig> runtimeConfig;

    public LHRecorder(RuntimeValue<LHRuntimeConfig> runtimeConfig) {
        this.runtimeConfig = runtimeConfig;
    }

    public void registerAndStartTask(
            LHTaskMethodRecordable recordable, ShutdownContext shutdownContext) {
        recordable.registerAndStartTask(shutdownContext);
    }

    public void registerLHWorkflow(LHWorkflowRecordable recordable) {
        if (!doesBeanExist(recordable.getBeanClass())) return;

        ConfigEvaluator configEvaluator = new ConfigEvaluator();
        String expandedName = configEvaluator.expand(recordable.getName()).asString();
        Optional<LHRuntimeConfig.WorkflowConfig> workflowConfig = Optional.ofNullable(
                getLHRuntimeConfig().specificWorkflowConfigs().get(expandedName));

        boolean registerWorkflow = workflowConfig
                .map(LHRuntimeConfig.WorkflowConfig::registerEnabled)
                .orElse(getLHRuntimeConfig().workflowsRegisterEnabled());

        if (!registerWorkflow) return;

        Workflow workflow = Workflow.newWorkflow(expandedName, thread -> {
            if (recordable.getBeanMethodName() == null) {
                LHWorkflowDefinition workflowDefinitionBean =
                        (LHWorkflowDefinition) getBean(recordable.getBeanClass());
                workflowDefinitionBean.define(thread);
                return;
            }

            try {
                Method method = recordable
                        .getBeanClass()
                        .getMethod(recordable.getBeanMethodName(), WorkflowThread.class);
                method.invoke(getBean(recordable.getBeanClass()), thread);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        String parent = recordable.getParent();
        if (parent != null) {
            workflow.setParent(configEvaluator.expand(parent).asString());
        }

        String defaultTaskTimeout = recordable.getDefaultTaskTimeout();
        if (defaultTaskTimeout != null) {
            workflow.setDefaultTaskTimeout(
                    configEvaluator.expand(defaultTaskTimeout).asInt());
        }

        String defaultTaskRetries = recordable.getDefaultTaskRetries();
        if (defaultTaskRetries != null) {
            workflow.setDefaultTaskRetries(
                    configEvaluator.expand(defaultTaskRetries).asInt());
        }

        String updateType = recordable.getUpdateType();
        if (updateType != null) {
            workflow.withUpdateType(AllowedUpdateType.valueOf(
                    configEvaluator.expand(updateType).asString().toUpperCase()));
        }

        String retention = recordable.getRetention();
        if (retention != null) {
            workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                    .setSecondsAfterWfTermination(
                            configEvaluator.expand(retention).asLong())
                    .build());
        }

        String defaultThreadRetention = recordable.getDefaultThreadRetention();
        if (defaultThreadRetention != null) {
            workflow.withDefaultThreadRetentionPolicy(ThreadRetentionPolicy.newBuilder()
                    .setSecondsAfterThreadTermination(
                            configEvaluator.expand(defaultThreadRetention).asLong())
                    .build());
        }

        LHExponentialBackoffRetryRecordable retryRecordable = recordable.getRetryRecordable();
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

        logRegistration(expandedName, LHWorkflow.class);
        workflow.registerWfSpec(getBlockingStub());
    }

    public void registerLHUserTaskForm(LHUserTaskFormRecordable recordable) {
        if (!doesBeanExist(recordable.getBeanClass())) return;

        ConfigEvaluator configEvaluator = new ConfigEvaluator();
        String expandedName = configEvaluator.expand(recordable.getName()).asString();
        Optional<LHRuntimeConfig.UserTaskConfig> taskConfig = Optional.ofNullable(
                getLHRuntimeConfig().specificUserTaskConfigs().get(expandedName));

        boolean registerUserTask = taskConfig
                .map(LHRuntimeConfig.UserTaskConfig::registerEnabled)
                .orElse(getLHRuntimeConfig().userTaskRegisterEnabled());

        if (!registerUserTask) return;

        UserTaskSchema schema = new UserTaskSchema(recordable.getBeanClass(), expandedName);
        PutUserTaskDefRequest request = schema.compile();

        logRegistration(expandedName, LHUserTaskForm.class);
        getBlockingStub().putUserTaskDef(request);
    }

    public void registerLHStructDef(LHStructDefRecordable recordable) {
        if (!doesBeanExist(recordable.getBeanClass())) return;

        ConfigEvaluator configEvaluator = new ConfigEvaluator();
        String expandedName = configEvaluator.expand(recordable.getName()).asString();
        Optional<LHRuntimeConfig.StructConfig> structConfig =
                Optional.ofNullable(getLHRuntimeConfig().specificStructConfigs().get(expandedName));

        boolean registerStruct = structConfig
                .map(LHRuntimeConfig.StructConfig::registerEnabled)
                .orElse(getLHRuntimeConfig().structsRegisterEnabled());

        if (!registerStruct) return;

        LHStructDefType structDefType = new LHStructDefType(recordable.getBeanClass());
        StructDefCompatibilityType compatibilityType = structConfig
                .map(LHRuntimeConfig.StructConfig::compatibility)
                .map(StructDefCompatibilityType::valueOf)
                .orElse(StructDefCompatibilityType.NO_SCHEMA_UPDATES);
        PutStructDefRequest.Builder builder = PutStructDefRequest.newBuilder()
                .setStructDef(structDefType.getInlineStructDef())
                .setName(expandedName)
                .setAllowedUpdates(compatibilityType);

        if (recordable.getDescription() != null) {
            builder.setDescription(recordable.getDescription());
        }

        logRegistration(expandedName, LHStructDef.class);
        getBlockingStub().putStructDef(builder.build());
    }

    private LHRuntimeConfig getLHRuntimeConfig() {
        return runtimeConfig.getValue();
    }

    private static void logRegistration(String expandedName, Class<?> classType) {
        log.info("Registering {}: {}", classType.getSimpleName(), expandedName);
    }

    private static LittleHorseBlockingStub getBlockingStub() {
        return getBean(LittleHorseBlockingStub.class);
    }

    private static <T> T getBean(Class<T> beanClass) {
        return CDI.current().select(beanClass).get();
    }

    private static <T> boolean doesBeanExist(Class<T> beanClass) {
        return CDI.current().select(beanClass).isResolvable();
    }
}

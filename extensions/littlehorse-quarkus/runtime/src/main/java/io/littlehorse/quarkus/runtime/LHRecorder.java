package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.runtime.health.LHTaskStatus;
import io.littlehorse.quarkus.runtime.recordable.LHRecordableDependenciesGraph;
import io.littlehorse.quarkus.runtime.recordable.LHStructDefRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHUserTaskFormRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowRecordable;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutStructDefRequest;
import io.littlehorse.sdk.common.proto.PutUserTaskDefRequest;
import io.littlehorse.sdk.common.proto.StructDefCompatibilityType;
import io.littlehorse.sdk.usertask.UserTaskSchema;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructDefType;
import io.littlehorse.sdk.worker.LHStructDef;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

import jakarta.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        if (!doesBeanExist(recordable.getBeanClass())) return;

        ConfigEvaluator configEvaluator = getBean(ConfigEvaluator.class);
        String expandedName = configEvaluator.expand(recordable.getName()).asString();
        Optional<LHRuntimeConfig.TaskConfig> taskConfig =
                Optional.ofNullable(getLHRuntimeConfig().specificTaskConfigs().get(expandedName));

        boolean registerTask = taskConfig
                .map(LHRuntimeConfig.TaskConfig::registerEnabled)
                .orElse(getLHRuntimeConfig().tasksRegisterEnabled());
        boolean startTask = taskConfig
                .map(LHRuntimeConfig.TaskConfig::startEnabled)
                .orElse(getLHRuntimeConfig().tasksStartEnabled());

        LHConfig config = getBean(LHConfig.class);
        LHTaskStatusesContainer taskStatusesContainer = getBean(LHTaskStatusesContainer.class);
        LHTaskWorker worker = new LHTaskWorker(
                getBean(recordable.getBeanClass()),
                recordable.getName(),
                config,
                configEvaluator.expand(recordable.getName()).getMembers());
        shutdownContext.addShutdownTask(new ShutdownContext.CloseRunnable(worker));

        if (registerTask) {
            logEvent("Registering", LHTaskMethod.class, expandedName);
            worker.registerTaskDef();
        }

        if (startTask) {
            taskStatusesContainer.add(new LHTaskStatus(worker));
            logEvent("Starting", LHTaskMethod.class, expandedName);
            worker.start();
        }
    }

    public void registerLHWorkflows(List<LHWorkflowRecordable> workflowRecordables) {
        LHRecordableDependenciesGraph<LHWorkflowRecordable> workflowRecordableGraph =
                new LHRecordableDependenciesGraph<>(workflowRecordables);
        workflowRecordableGraph.toOrderedList().forEach(this::registerLHWorkflow);
    }

    public void registerLHWorkflow(LHWorkflowRecordable recordable) {
        if (!doesBeanExist(recordable.getBeanClass())) return;

        ConfigEvaluator configEvaluator = getBean(ConfigEvaluator.class);
        String expandedName = configEvaluator.expand(recordable.getName()).asString();
        Optional<LHRuntimeConfig.WorkflowConfig> workflowConfig = Optional.ofNullable(
                getLHRuntimeConfig().specificWorkflowConfigs().get(expandedName));

        boolean registerWorkflow = workflowConfig
                .map(LHRuntimeConfig.WorkflowConfig::registerEnabled)
                .orElse(getLHRuntimeConfig().workflowsRegisterEnabled());

        if (!registerWorkflow) return;

        Workflow workflow = recordable.toWorkflow();

        logEvent("Registering", LHWorkflow.class, expandedName);

        LHConfig config = getBean(LHConfig.class);
        workflow.registerWfSpec(config);
    }

    public void registerLHUserTaskForm(LHUserTaskFormRecordable recordable) {
        if (!doesBeanExist(recordable.getBeanClass())) return;

        ConfigEvaluator configEvaluator = getBean(ConfigEvaluator.class);
        String expandedName = configEvaluator.expand(recordable.getName()).asString();
        Optional<LHRuntimeConfig.UserTaskConfig> taskConfig = Optional.ofNullable(
                getLHRuntimeConfig().specificUserTaskConfigs().get(expandedName));

        boolean registerUserTask = taskConfig
                .map(LHRuntimeConfig.UserTaskConfig::registerEnabled)
                .orElse(getLHRuntimeConfig().userTaskRegisterEnabled());

        if (!registerUserTask) return;

        UserTaskSchema schema = new UserTaskSchema(recordable.getBeanClass(), expandedName);
        PutUserTaskDefRequest request = schema.compile();

        logEvent("Registering", LHUserTaskForm.class, expandedName);
        getBlockingStub().putUserTaskDef(request);
    }

    public void registerLHStructDefs(List<LHStructDefRecordable> structDefRecordables) {
        LHRecordableDependenciesGraph<LHStructDefRecordable> structDefRecordableGraph =
                new LHRecordableDependenciesGraph<>(structDefRecordables);
        structDefRecordableGraph.toOrderedList().forEach(this::registerLHStructDef);
    }

    public void registerLHStructDef(LHStructDefRecordable recordable) {
        if (!doesBeanExist(recordable.getBeanClass())) return;

        ConfigEvaluator configEvaluator = getBean(ConfigEvaluator.class);
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
                .orElse(StructDefCompatibilityType.NO_SCHEMA_UPDATES);
        PutStructDefRequest.Builder builder = PutStructDefRequest.newBuilder()
                .setStructDef(structDefType.getInlineStructDef())
                .setName(expandedName)
                .setAllowedUpdates(compatibilityType);

        if (recordable.getDescription() != null) {
            builder.setDescription(recordable.getDescription());
        }

        logEvent("Registering", LHStructDef.class, expandedName);
        getBlockingStub().putStructDef(builder.build());
    }

    private LHRuntimeConfig getLHRuntimeConfig() {
        return runtimeConfig.getValue();
    }

    private static void logEvent(String type, Class<?> classType, String expandedName) {
        log.info("{} {}: {}", type, classType.getSimpleName(), expandedName);
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

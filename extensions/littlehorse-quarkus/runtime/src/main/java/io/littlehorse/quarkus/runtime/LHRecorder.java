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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Recorder
public class LHRecorder {
    private static final Logger log = LoggerFactory.getLogger(LHRecorder.class);
    private final RuntimeValue<LHRuntimeConfig> runtimeConfig;

    public LHRecorder(RuntimeValue<LHRuntimeConfig> runtimeConfig) {
        this.runtimeConfig = runtimeConfig;
    }

    public void registerAndStartTasks(
            List<LHTaskMethodRecordable> taskMethodRecordables,
            List<LHStructDefRecordable> structDefRecordables,
            ShutdownContext shutdownContext) {
        // Tasks may accept or return StructDef types whose names contain `${...}` placeholders, so
        // the StructDef placeholder values must be available when resolving task parameter/return
        // types.
        ConfigEvaluator configEvaluator = getBean(ConfigEvaluator.class);
        Map<String, String> structPlaceholderValues =
                computeStructPlaceholderValues(structDefRecordables);
        taskMethodRecordables.stream()
                .filter(recordable -> doesBeanExist(recordable.getBeanClass()))
                .forEach(recordable -> {
                    Map<String, String> placeholderValues = computeTaskPlaceholderValues(
                            configEvaluator, structPlaceholderValues, recordable);
                    recordable.setPlaceholderValues(placeholderValues);
                    registerAndStartTask(recordable, shutdownContext);
                });
    }

    static Map<String, String> computeTaskPlaceholderValues(
            ConfigEvaluator configEvaluator,
            Map<String, String> structPlaceholderValues,
            LHTaskMethodRecordable recordable) {
        Map<String, String> placeholderValues = new LinkedHashMap<>();
        mergePlaceholderValues(placeholderValues, structPlaceholderValues);
        mergeTemplatePlaceholderValues(placeholderValues, configEvaluator, recordable.getName());
        recordable.getStructDefNameTemplates().stream()
                .sorted()
                .forEach(template -> mergeTemplatePlaceholderValues(
                        placeholderValues, configEvaluator, template));
        return Map.copyOf(placeholderValues);
    }

    private static void mergeTemplatePlaceholderValues(
            Map<String, String> placeholderValues,
            ConfigEvaluator configEvaluator,
            String template) {
        ConfigEvaluator.ConfigExpression expanded = configEvaluator.expand(template);
        mergePlaceholderValues(placeholderValues, expanded.getMembers());
    }

    private static void mergePlaceholderValues(
            Map<String, String> placeholderValues, Map<String, String> additions) {
        additions.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        mergePlaceholderValue(placeholderValues, entry.getKey(), entry.getValue()));
    }

    private static void mergePlaceholderValue(
            Map<String, String> placeholderValues, String key, String value) {
        String existingValue = placeholderValues.putIfAbsent(key, value);
        if (existingValue != null && !existingValue.equals(value)) {
            throw new IllegalStateException(
                    "Conflicting values for configuration placeholder '%s': '%s' and '%s'"
                            .formatted(key, existingValue, value));
        }
    }

    private void registerAndStartTask(
            LHTaskMethodRecordable recordable, ShutdownContext shutdownContext) {

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
                recordable.getPlaceholderValues());
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

    public void registerLHWorkflows(
            List<LHWorkflowRecordable> workflowRecordables,
            List<LHStructDefRecordable> structDefRecordables) {
        // Workflows may reference StructDefs (e.g. via declareStruct) whose names contain `${...}`
        // placeholders, so they must be resolved with the same placeholder values as the
        // StructDefs.
        Map<String, String> placeholderValues =
                computeStructPlaceholderValues(structDefRecordables);
        List<LHWorkflowRecordable> existingRecordables = workflowRecordables.stream()
                .filter(recordable -> doesBeanExist(recordable.getBeanClass()))
                .toList();
        existingRecordables.forEach(
                recordable -> recordable.setPlaceholderValues(placeholderValues));

        LHRecordableDependenciesGraph<LHWorkflowRecordable> workflowRecordableGraph =
                new LHRecordableDependenciesGraph<>(existingRecordables);
        workflowRecordableGraph.toOrderedList().forEach(this::registerLHWorkflow);
    }

    private void registerLHWorkflow(LHWorkflowRecordable recordable) {
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

    public void registerLHUserTaskForms(List<LHUserTaskFormRecordable> userTaskFormRecordables) {
        userTaskFormRecordables.stream()
                .filter(recordable -> doesBeanExist(recordable.getBeanClass()))
                .forEach(this::registerLHUserTaskForm);
    }

    private void registerLHUserTaskForm(LHUserTaskFormRecordable recordable) {
        ConfigEvaluator configEvaluator = getBean(ConfigEvaluator.class);
        String expandedName = configEvaluator.expand(recordable.getName()).asString();
        Optional<LHRuntimeConfig.UserTaskConfig> taskConfig = Optional.ofNullable(
                getLHRuntimeConfig().specificUserTaskConfigs().get(expandedName));

        boolean registerUserTask = taskConfig
                .map(LHRuntimeConfig.UserTaskConfig::registerEnabled)
                .orElse(getLHRuntimeConfig().userTaskRegisterEnabled());

        if (!registerUserTask) return;

        UserTaskSchema schema =
                new UserTaskSchema(getBean(recordable.getBeanClass()), expandedName);
        PutUserTaskDefRequest request = schema.compile();

        logEvent("Registering", LHUserTaskForm.class, expandedName);
        getBlockingStub().putUserTaskDef(request);
    }

    public void registerLHStructDefs(List<LHStructDefRecordable> structDefRecordables) {
        List<LHStructDefRecordable> existingRecordables = structDefRecordables.stream()
                .filter(recordable -> doesBeanExist(recordable.getBeanClass()))
                .toList();

        // Combine the placeholder values of every StructDef so that placeholders in nested
        // StructDef names (e.g. a parent referencing a nested StructDef by its `${...}` name) can
        // be
        // resolved.
        Map<String, String> placeholderValues = computeStructPlaceholderValues(existingRecordables);
        existingRecordables.forEach(
                recordable -> recordable.setPlaceholderValues(placeholderValues));

        LHRecordableDependenciesGraph<LHStructDefRecordable> structDefRecordableGraph =
                new LHRecordableDependenciesGraph<>(existingRecordables);
        structDefRecordableGraph
                .toOrderedList()
                .forEach(recordable -> registerLHStructDef(recordable, placeholderValues));
    }

    private Map<String, String> computeStructPlaceholderValues(
            List<LHStructDefRecordable> structDefRecordables) {
        ConfigEvaluator configEvaluator = getBean(ConfigEvaluator.class);
        Map<String, String> placeholderValues = new LinkedHashMap<>();
        structDefRecordables.stream()
                .map(LHStructDefRecordable::getName)
                .sorted()
                .forEach(template -> mergeTemplatePlaceholderValues(
                        placeholderValues, configEvaluator, template));
        return Map.copyOf(placeholderValues);
    }

    private void registerLHStructDef(
            LHStructDefRecordable recordable, Map<String, String> placeholderValues) {
        ConfigEvaluator configEvaluator = getBean(ConfigEvaluator.class);
        String expandedName = configEvaluator.expand(recordable.getName()).asString();
        Optional<LHRuntimeConfig.StructConfig> structConfig =
                Optional.ofNullable(getLHRuntimeConfig().specificStructConfigs().get(expandedName));

        boolean registerStruct = structConfig
                .map(LHRuntimeConfig.StructConfig::registerEnabled)
                .orElse(getLHRuntimeConfig().structsRegisterEnabled());

        if (!registerStruct) return;

        LHConfig config = getBean(LHConfig.class);
        LHStructDefType structDefType = new LHStructDefType(
                recordable.getBeanClass(), config.getTypeAdapterRegistry(), placeholderValues);
        StructDefCompatibilityType compatibilityType = structConfig
                .map(LHRuntimeConfig.StructConfig::compatibility)
                .orElse(StructDefCompatibilityType.NO_SCHEMA_UPDATES);
        PutStructDefRequest.Builder builder = structDefType.toPutStructDefRequest().toBuilder()
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

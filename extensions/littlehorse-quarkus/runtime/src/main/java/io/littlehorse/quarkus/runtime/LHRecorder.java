package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.runtime.recordable.LHStructDefRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHUserTaskFormRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowRecordable;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutStructDefRequest;
import io.littlehorse.sdk.common.proto.PutUserTaskDefRequest;
import io.littlehorse.sdk.common.proto.StructDefCompatibilityType;
import io.littlehorse.sdk.usertask.UserTaskSchema;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructDefType;
import io.littlehorse.sdk.worker.LHStructDef;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

import jakarta.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        recordable.registerWorkflow();
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

package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.runtime.recordable.LHStructDefRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHUserTaskFormRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowRecordable;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutStructDefRequest;
import io.littlehorse.sdk.common.proto.StructDefCompatibilityType;
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
        recordable.registerUserTask();
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

        LittleHorseBlockingStub blockingStub = getBlockingStub();
        log.info("Registering {}: {}", LHStructDef.class.getSimpleName(), expandedName);
        blockingStub.putStructDef(builder.build());
    }

    private LHRuntimeConfig getLHRuntimeConfig() {
        return runtimeConfig.getValue();
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

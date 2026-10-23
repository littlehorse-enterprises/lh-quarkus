package io.littlehorse.quarkus.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.runtime.recordable.LHStructDefRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHUserTaskDefRecordable;
import io.littlehorse.sdk.common.adapter.LHTypeAdapterRegistry;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.*;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.worker.LHStructDef;
import io.quarkus.runtime.RuntimeValue;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class LHUserTaskRegistrationTest {
    private static final String SCHEMA_TEMPLATE = "${approval.schema:approval-result}";

    @Test
    void shouldRegisterUserTaskWithTheReturnedSchemaVersionAndResolvedNames() {
        try (Context context = new Context()) {
            RuntimeValue<Map<String, StructDefId>> schemas = context.registerStructs();
            context.recorder.registerLHUserTaskDefs(
                    List.of(task("${approval.task:approve}")), schemas);

            ArgumentCaptor<PutUserTaskDefRequest> request =
                    ArgumentCaptor.forClass(PutUserTaskDefRequest.class);
            var order = inOrder(context.stub);
            order.verify(context.stub).putStructDef(any());
            order.verify(context.stub).putUserTaskDef(request.capture());
            assertThat(request.getValue())
                    .isEqualTo(PutUserTaskDefRequest.newBuilder()
                            .setName("approve")
                            .setResultStructDefId(context.schemaId)
                            .build());
            assertThat(schemas.getValue())
                    .containsExactly(Map.entry("approval-result", context.schemaId));
        }
    }

    @Test
    void shouldReuseOneStructDefForMultipleUserTasks() {
        try (Context context = new Context()) {
            var schemas = context.registerStructs();
            context.recorder.registerLHUserTaskDefs(
                    List.of(task("approve"), task("review")), schemas);

            var requests = ArgumentCaptor.forClass(PutUserTaskDefRequest.class);
            verify(context.stub, times(1)).putStructDef(any());
            verify(context.stub, times(2)).putUserTaskDef(requests.capture());
            assertThat(requests.getAllValues())
                    .extracting(PutUserTaskDefRequest::getName)
                    .containsExactly("approve", "review");
            assertThat(requests.getAllValues()).allSatisfy(request -> {
                assertThat(request.getResultStructDefId()).isEqualTo(context.schemaId);
                assertThat(request.getFieldsCount()).isZero();
            });
        }
    }

    @Test
    void shouldFailClearlyWhenResultStructRegistrationIsDisabled() {
        try (Context context = new Context()) {
            when(context.config.structsRegisterEnabled()).thenReturn(false);
            var schemas = context.registerStructs();
            assertThatThrownBy(() -> context.recorder.registerLHUserTaskDefs(
                            List.of(task("approve")), schemas))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("approve", "approval-result", "was not registered");
            verifyNoInteractions(context.stub);
        }
    }

    @Test
    void shouldSkipDisabledUserTaskWithoutRequiringItsResultStruct() {
        try (Context context = new Context()) {
            LHRuntimeConfig.UserTaskConfig taskConfig = mock(LHRuntimeConfig.UserTaskConfig.class);
            when(taskConfig.registerEnabled()).thenReturn(false);
            when(context.config.specificUserTaskConfigs())
                    .thenReturn(Map.of("approve", taskConfig));
            context.recorder.registerLHUserTaskDefs(
                    List.of(task("approve")), new RuntimeValue<>(Map.of()));
            verifyNoInteractions(context.stub);
        }
    }

    @Test
    void shouldRespectGlobalRegistrationDisabled() {
        try (Context context = new Context()) {
            when(context.config.userTaskRegisterEnabled()).thenReturn(false);
            context.recorder.registerLHUserTaskDefs(
                    List.of(task("approve")), new RuntimeValue<>(Map.of()));
            verifyNoInteractions(context.stub);
        }
    }

    @Test
    void shouldHonorStructCompatibilityConfiguration() {
        try (Context context = new Context()) {
            LHRuntimeConfig.StructConfig config = mock(LHRuntimeConfig.StructConfig.class);
            when(config.registerEnabled()).thenReturn(true);
            when(config.compatibility())
                    .thenReturn(StructDefCompatibilityType.FULLY_COMPATIBLE_SCHEMA_UPDATES);
            when(context.config.specificStructConfigs())
                    .thenReturn(Map.of("approval-result", config));
            context.registerStructs();
            var request = ArgumentCaptor.forClass(PutStructDefRequest.class);
            verify(context.stub).putStructDef(request.capture());
            assertThat(request.getValue().getAllowedUpdates())
                    .isEqualTo(StructDefCompatibilityType.FULLY_COMPATIBLE_SCHEMA_UPDATES);
        }
    }

    private static LHUserTaskDefRecordable task(String name) {
        return new LHUserTaskDefRecordable(Definition.class, name, SCHEMA_TEMPLATE);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static class Context implements AutoCloseable {
        final LHRuntimeConfig config = mock(LHRuntimeConfig.class);
        final LittleHorseBlockingStub stub = mock(LittleHorseBlockingStub.class);
        final StructDefId schemaId = StructDefId.newBuilder()
                .setName("approval-result")
                .setVersion(7)
                .build();
        final LHRecorder recorder = new LHRecorder(new RuntimeValue<>(config));
        final MockedStatic<CDI> mockedCdi;

        Context() {
            when(config.specificStructConfigs()).thenReturn(Map.of());
            when(config.specificUserTaskConfigs()).thenReturn(Map.of());
            when(config.structsRegisterEnabled()).thenReturn(true);
            when(config.userTaskRegisterEnabled()).thenReturn(true);
            when(stub.putStructDef(any()))
                    .thenReturn(StructDef.newBuilder().setId(schemaId).build());
            LHConfig sdkConfig = mock(LHConfig.class);
            when(sdkConfig.getTypeAdapterRegistry()).thenReturn(LHTypeAdapterRegistry.empty());
            Map<Class<?>, Object> beans = new LinkedHashMap<>();
            beans.put(ConfigEvaluator.class, new ConfigEvaluator(ConfigProvider.getConfig()));
            beans.put(LHConfig.class, sdkConfig);
            beans.put(LittleHorseBlockingStub.class, stub);
            beans.put(Result.class, new Result());
            beans.put(Definition.class, new Definition());
            CDI<Object> cdi = mock(CDI.class);
            when(cdi.select(any(Class.class), any(Annotation[].class))).thenAnswer(invocation -> {
                Class<?> type = invocation.getArgument(0);
                Instance<Object> instance = mock(Instance.class);
                when(instance.isResolvable()).thenReturn(beans.containsKey(type));
                when(instance.get()).thenReturn(beans.get(type));
                return instance;
            });
            mockedCdi = mockStatic(CDI.class);
            mockedCdi.when(CDI::current).thenReturn(cdi);
        }

        RuntimeValue<Map<String, StructDefId>> registerStructs() {
            return recorder.registerLHStructDefs(
                    List.of(new LHStructDefRecordable(Result.class, SCHEMA_TEMPLATE, null)));
        }

        @Override
        public void close() {
            mockedCdi.close();
        }
    }

    public static class Definition {}

    @LHStructDef(SCHEMA_TEMPLATE)
    public static class Result {
        private Boolean approved;

        public Boolean getApproved() {
            return approved;
        }

        public void setApproved(Boolean approved) {
            this.approved = approved;
        }
    }
}

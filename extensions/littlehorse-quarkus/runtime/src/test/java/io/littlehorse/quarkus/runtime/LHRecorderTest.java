package io.littlehorse.quarkus.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.runtime.recordable.LHStructDefRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.littlehorse.sdk.worker.internal.util.PlaceholderUtil;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.smallrye.config.SmallRyeConfig;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class LHRecorderTest {

    private ConfigEvaluator configEvaluator;

    @BeforeEach
    void setUp() {
        configEvaluator =
                new ConfigEvaluator(ConfigProvider.getConfig().unwrap(SmallRyeConfig.class));
    }

    @Test
    void shouldResolveRawInlineStructInputAndOutputPlaceholdersWithoutLocalStructDefs() {
        LHTaskMethodRecordable recordable = recordable(
                "${raw.task.name}", "${raw.input.struct.name}", "${raw.output.struct.name}");

        Map<String, String> values = startTask(recordable, List.of());

        assertThat(values)
                .containsEntry("raw.task.name", "raw-task")
                .containsEntry("raw.input.struct.name", "configured-request")
                .containsEntry("raw.output.struct.name", "configured-response");
        assertThat(PlaceholderUtil.replacePlaceholders("${raw.task.name}", values))
                .isEqualTo("raw-task");
        assertThat(PlaceholderUtil.replacePlaceholders("${raw.input.struct.name}", values))
                .isEqualTo("configured-request");
        assertThat(PlaceholderUtil.replacePlaceholders("${raw.output.struct.name}", values))
                .isEqualTo("configured-response");
    }

    @Test
    void shouldFailWithMissingLHTypeConfigurationKey() {
        LHTaskMethodRecordable recordable =
                recordable("raw-task", "${missing.raw.input.struct.name}");

        assertThatThrownBy(() -> startTask(recordable, List.of()))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessageContaining("missing.raw.input.struct.name");
    }

    @Test
    void shouldPreservePlaceholderValuesFromConfiguredStructDefs() {
        LHTaskMethodRecordable recordable = recordable("raw-task", "${my.config.test}");
        List<LHStructDefRecordable> structDefs =
                List.of(structDef("${my.config.test}"), structDef("${nested.struct.name}"));

        Map<String, String> values = startTask(recordable, structDefs);

        assertThat(values)
                .containsEntry("my.config.test", "this-is-a-test")
                .containsEntry("nested.struct.name", "nested");
    }

    @Test
    void shouldHandleTheSamePlaceholderFromMultipleLocationsDeterministically() {
        LHTaskMethodRecordable recordable = recordable("${my.config.test}", "${my.config.test}");

        assertThat(startTask(recordable, List.of(structDef("${my.config.test}"))))
                .containsExactly(Map.entry("my.config.test", "this-is-a-test"));
    }

    private static LHTaskMethodRecordable recordable(
            String taskName, String... structDefNameTemplates) {
        return new LHTaskMethodRecordable(
                RawInlineStructTask.class, taskName, null, Set.of(structDefNameTemplates));
    }

    private static LHStructDefRecordable structDef(String name) {
        return new LHStructDefRecordable(RawInlineStructTask.class, name, null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Map<String, String> startTask(
            LHTaskMethodRecordable recordable, List<LHStructDefRecordable> structDefs) {
        LHRuntimeConfig runtimeConfig = mock(LHRuntimeConfig.class);
        when(runtimeConfig.specificTaskConfigs()).thenReturn(Map.of());
        when(runtimeConfig.tasksRegisterEnabled()).thenReturn(false);
        when(runtimeConfig.tasksStartEnabled()).thenReturn(false);

        Map<Class<?>, Object> beans = new LinkedHashMap<>();
        beans.put(ConfigEvaluator.class, configEvaluator);
        beans.put(LHConfig.class, mock(LHConfig.class));
        beans.put(LHTaskStatusesContainer.class, mock(LHTaskStatusesContainer.class));
        beans.put(RawInlineStructTask.class, new RawInlineStructTask());

        CDI<Object> cdi = mock(CDI.class);
        when(cdi.select(any(Class.class), any(Annotation[].class))).thenAnswer(invocation -> {
            Class<?> beanClass = invocation.getArgument(0);
            Instance<Object> instance = mock(Instance.class);
            when(instance.isResolvable()).thenReturn(beans.containsKey(beanClass));
            when(instance.get()).thenReturn(beans.get(beanClass));
            return instance;
        });

        List<List<?>> constructorArguments = new ArrayList<>();
        try (MockedStatic<CDI> mockedCdi = mockStatic(CDI.class);
                MockedConstruction<LHTaskWorker> workers = mockConstruction(
                        LHTaskWorker.class,
                        (worker, context) -> constructorArguments.add(context.arguments()))) {
            mockedCdi.when(CDI::current).thenReturn(cdi);

            LHRecorder recorder = new LHRecorder(new RuntimeValue<>(runtimeConfig));
            recorder.registerAndStartTasks(
                    List.of(recordable), structDefs, mock(ShutdownContext.class));

            assertThat(workers.constructed()).hasSize(1);
            return (Map<String, String>) constructorArguments.get(0).get(3);
        }
    }

    static class RawInlineStructTask {}
}

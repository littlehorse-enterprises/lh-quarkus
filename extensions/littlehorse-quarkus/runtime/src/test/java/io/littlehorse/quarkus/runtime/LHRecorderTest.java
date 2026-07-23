package io.littlehorse.quarkus.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.littlehorse.sdk.worker.internal.util.PlaceholderUtil;
import io.smallrye.config.SmallRyeConfig;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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

        Map<String, String> values =
                LHRecorder.computeTaskPlaceholderValues(configEvaluator, Map.of(), recordable);

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

        assertThatThrownBy(() -> LHRecorder.computeTaskPlaceholderValues(
                        configEvaluator, Map.of(), recordable))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessageContaining("missing.raw.input.struct.name");
    }

    @Test
    void shouldPreservePlaceholderValuesFromConfiguredStructDefs() {
        LHTaskMethodRecordable recordable = recordable("raw-task", "${my.config.test}");

        Map<String, String> values = LHRecorder.computeTaskPlaceholderValues(
                configEvaluator,
                Map.of("my.config.test", "this-is-a-test", "nested.struct.name", "nested"),
                recordable);

        assertThat(values)
                .containsEntry("my.config.test", "this-is-a-test")
                .containsEntry("nested.struct.name", "nested");
    }

    @Test
    void shouldFailDeterministicallyForConflictingPlaceholderValues() {
        LHTaskMethodRecordable recordable = recordable("raw-task", "${my.config.test}");

        assertThatThrownBy(() -> LHRecorder.computeTaskPlaceholderValues(
                        configEvaluator,
                        Map.of("my.config.test", "configured-by-struct"),
                        recordable))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Conflicting values for configuration placeholder "
                        + "'my.config.test': 'configured-by-struct' and 'this-is-a-test'");
    }

    private static LHTaskMethodRecordable recordable(
            String taskName, String... structDefNameTemplates) {
        return new LHTaskMethodRecordable(
                RawInlineStructTask.class, taskName, null, List.of(structDefNameTemplates));
    }

    static class RawInlineStructTask {}
}

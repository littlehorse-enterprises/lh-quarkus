package io.littlehorse.quarkus.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.littlehorse.sdk.common.config.LHConfig;
import io.smallrye.config.ConfigValue;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class LHBeansTest {
    LHBeans lhBeans;
    Config mockConfig;

    @BeforeEach
    void beforeEach() {
        mockConfig = mock(Config.class);
        lhBeans = new LHBeans();
    }

    @Test
    void shouldParseConfigurationsToLHConfig() {
        String hostProperty = "lhc.api.host";
        String expectedHost = "test-my.host.com";
        String workerProperty = "lhw.task-worker.version";
        String expectedVersion = "v0.0.0.metronome.local.test";

        when(mockConfig.getPropertyNames()).thenReturn(List.of(workerProperty, hostProperty));
        when(mockConfig.getConfigValue(hostProperty))
                .thenReturn(ConfigValue.builder()
                        .withName(hostProperty)
                        .withValue(expectedHost)
                        .build());
        when(mockConfig.getConfigValue(workerProperty))
                .thenReturn(ConfigValue.builder()
                        .withName(workerProperty)
                        .withValue(expectedVersion)
                        .build());

        LHConfig configuration = lhBeans.configuration(mockConfig);

        assertThat(configuration.getApiBootstrapHost()).isEqualTo(expectedHost);
        assertThat(configuration.getTaskWorkerVersion()).isEqualTo(expectedVersion);
    }

    @Test
    void shouldParseConfigurationsToLHConfigFromEnvFormat() {
        String hostProperty = "LHC_API_HOST";
        String expectedHost = "test-my.host.com";
        String workerProperty = "LHW_TASK_WORKER_VERSION";
        String expectedVersion = "v0.0.0.metronome.local.test";

        when(mockConfig.getPropertyNames()).thenReturn(List.of(workerProperty, hostProperty));
        when(mockConfig.getConfigValue(hostProperty))
                .thenReturn(ConfigValue.builder()
                        .withName(hostProperty)
                        .withValue(expectedHost)
                        .build());
        when(mockConfig.getConfigValue(workerProperty))
                .thenReturn(ConfigValue.builder()
                        .withName(workerProperty)
                        .withValue(expectedVersion)
                        .build());

        LHConfig configuration = lhBeans.configuration(mockConfig);

        assertThat(configuration.getApiBootstrapHost()).isEqualTo(expectedHost);
        assertThat(configuration.getTaskWorkerVersion()).isEqualTo(expectedVersion);
    }

    @Test
    void shouldMergeConfigurationsToLHConfig() {
        String hostProperty = "LHC_API_HOST";
        String expectedHost1 = "test-my.host.com";
        String expectedHost2 = "test-my.second-host.com";

        when(mockConfig.getPropertyNames()).thenReturn(List.of(hostProperty, hostProperty));
        when(mockConfig.getConfigValue(hostProperty))
                .thenReturn(
                        ConfigValue.builder()
                                .withName(hostProperty)
                                .withValue(expectedHost1)
                                .build(),
                        ConfigValue.builder()
                                .withName(hostProperty)
                                .withValue(expectedHost2)
                                .build());

        LHConfig configuration = lhBeans.configuration(mockConfig);

        assertThat(configuration.getApiBootstrapHost()).isEqualTo(expectedHost2);
    }
}

package io.littlehorse.quarkus.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.sdk.common.adapter.LHLongAdapter;
import io.littlehorse.sdk.common.adapter.LHStringAdapter;
import io.littlehorse.sdk.common.adapter.LHTypeAdapter;
import io.littlehorse.sdk.common.config.LHConfig;
import io.smallrye.config.ConfigValue;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class LHExternalBeansTest {
    LHExternalBeans lhBeans;
    Config mockConfig;

    @BeforeEach
    void beforeEach() {
        mockConfig = mock(Config.class);
        lhBeans = new LHExternalBeans();
    }

    static class MyCustomAdapter1 implements LHStringAdapter<UUID> {

        @Override
        public String toString(UUID src) {
            return "";
        }

        @Override
        public UUID fromString(String src) {
            return null;
        }

        @Override
        public Class<UUID> getTypeClass() {
            return UUID.class;
        }
    }

    static class MyCustomAdapter2 extends LHLongAdapter<Long> {

        @Override
        public Long toLong(Long src) {
            return 0L;
        }

        @Override
        public Long fromLong(Long src) {
            return 0L;
        }

        @Override
        public Class<Long> getTypeClass() {
            return Long.class;
        }
    }

    @Test
    void shouldRegisterTypeAdapters() {
        LHRuntimeConfig runtimeConfig = mock(LHRuntimeConfig.class);
        when(runtimeConfig.specificTypeAdapterConfigs())
                .thenReturn(Map.of(
                        MyCustomAdapter1.class.getName(), () -> true,
                        MyCustomAdapter2.class.getName(), () -> false));

        MyCustomAdapter1 myCustomAdapter1 = new MyCustomAdapter1();
        List<LHTypeAdapter<?>> adapters = List.of(myCustomAdapter1, new MyCustomAdapter2());

        LHConfig configuration = lhBeans.configuration(runtimeConfig, mockConfig, adapters);

        assertThat(configuration.getTypeAdapterRegistry().asMap())
                .isEqualTo(Map.of(UUID.class, myCustomAdapter1));
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

        LHConfig configuration = lhBeans.configuration(null, mockConfig, null);

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

        LHConfig configuration = lhBeans.configuration(null, mockConfig, null);

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

        LHConfig configuration = lhBeans.configuration(null, mockConfig, null);

        assertThat(configuration.getApiBootstrapHost()).isEqualTo(expectedHost2);
    }
}

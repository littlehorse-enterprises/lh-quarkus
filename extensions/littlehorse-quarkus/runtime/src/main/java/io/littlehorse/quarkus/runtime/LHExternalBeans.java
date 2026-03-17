package io.littlehorse.quarkus.runtime;

import com.google.common.collect.Streams;

import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.sdk.common.adapter.LHTypeAdapter;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.config.LHConfig.LHConfigBuilder;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseFutureStub;
import io.quarkus.arc.All;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

@ApplicationScoped
public class LHExternalBeans {

    private static final Logger log = LoggerFactory.getLogger(LHExternalBeans.class);

    private static final class ServerConfig {
        private static final String CONFIG_PREFIX_REGEX = "^(LHC_|LHW_)[A-Z_]+";
        private final String key;
        private final Object value;

        private ServerConfig(String key, Object value) {
            this.value = value;
            this.key = key == null ? null : key.strip().toUpperCase().replaceAll("[.-]", "_");
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        boolean isValid() {
            if (value == null) return false;
            if (key == null) return false;
            if (!key.matches(CONFIG_PREFIX_REGEX)) return false;
            return LHConfig.configNames().contains(key);
        }
    }

    @Produces
    @DefaultBean
    @Singleton
    @Unremovable
    LHConfig configuration(
            LHRuntimeConfig runtimeConfig, Config config, @All List<LHTypeAdapter<?>> adapters) {
        Properties properties = new Properties();
        Streams.stream(config.getPropertyNames())
                .map(propertyName -> new ServerConfig(
                        propertyName, config.getConfigValue(propertyName).getValue()))
                .filter(ServerConfig::isValid)
                .forEach(serverConfig ->
                        properties.put(serverConfig.getKey(), serverConfig.getValue()));

        LHConfigBuilder lhConfigBuilder = LHConfig.newBuilder().loadFromProperties(properties);

        if (adapters != null) {
            adapters.stream()
                    .filter(lhTypeAdapter -> Optional.ofNullable(runtimeConfig
                                    .specificTypeAdapterConfigs()
                                    .get(lhTypeAdapter.getClass().getName()))
                            .map(LHRuntimeConfig.TypeAdapterConfig::registerEnabled)
                            .orElse(runtimeConfig.typeAdaptersRegisterEnabled()))
                    .forEach(lhTypeAdapter -> {
                        log.info(
                                "Registering {}: {}",
                                LHTypeAdapter.class.getSimpleName(),
                                lhTypeAdapter.getClass().getName());
                        lhConfigBuilder.addTypeAdapter(lhTypeAdapter);
                    });
        }

        return lhConfigBuilder.build();
    }

    @Produces
    @DefaultBean
    @Singleton
    @Unremovable
    LittleHorseBlockingStub blockingStub(LHConfig config) {
        return config.getBlockingStub();
    }

    @Produces
    @DefaultBean
    @Singleton
    @Unremovable
    LittleHorseFutureStub futureStub(LHConfig config) {
        return config.getFutureStub();
    }
}

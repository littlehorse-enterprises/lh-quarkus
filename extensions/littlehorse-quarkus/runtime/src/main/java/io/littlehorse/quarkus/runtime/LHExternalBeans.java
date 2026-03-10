package io.littlehorse.quarkus.runtime;

import com.google.common.collect.Streams;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseFutureStub;
import io.littlehorse.sdk.worker.adapter.LHTypeAdapter;
import io.quarkus.arc.All;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@ApplicationScoped
public class LHExternalBeans {

    private static final Logger log = LoggerFactory.getLogger(LHExternalBeans.class);

    @Produces
    @DefaultBean
    @Singleton
    @Unremovable
    LHConfig configuration(Config config, @All List<LHTypeAdapter<?>> adapters) {
        Properties properties = new Properties();
        Streams.stream(config.getPropertyNames())
                .map(propertyName -> new ServerConfig(
                        propertyName, config.getConfigValue(propertyName).getValue()))
                .filter(ServerConfig::isValid)
                .forEach(serverConfig ->
                        properties.put(serverConfig.getKey(), serverConfig.getValue()));

        // TODO: wait until this is done
        // https://github.com/littlehorse-enterprises/littlehorse/pull/2136/changes

        LHConfig lhConfig = LHConfig.newBuilder().loadFromProperties(properties).build();

        Optional.ofNullable(adapters).orElse(Collections.emptyList()).forEach(lhTypeAdapter -> {
            log.info(
                    "Registering {}: {}",
                    LHTypeAdapter.class.getSimpleName(),
                    lhTypeAdapter.getClass().getName());
            lhConfig.registerTypeAdapter(lhTypeAdapter);
        });

        return lhConfig;
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

    private static final class ServerConfig {
        private static final String CONFIG_PREFIX_REGEX = "^(LHC_|LHW_)[A-Z_]+";
        private final String key;
        private final Object value;

        private ServerConfig(String key, Object value) {
            this.value = value;
            this.key = key == null ? null : key.strip().toUpperCase().replaceAll("[.-]", "_");
        }

        boolean isValid() {
            if (value == null) return false;
            if (key == null) return false;
            if (!key.matches(CONFIG_PREFIX_REGEX)) return false;
            return LHConfig.configNames().contains(key);
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }
    }
}

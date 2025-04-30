package io.littlehorse.quarkus.runtime;

import com.google.common.collect.Streams;

import io.littlehorse.sdk.common.config.LHConfig;
import io.quarkus.arc.DefaultBean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.eclipse.microprofile.config.Config;

import java.util.Properties;

@ApplicationScoped
public class LittleHorseBeans {

    // https://quarkus.io/guides/config-reference
    @Produces
    @DefaultBean
    @ApplicationScoped
    LHConfig configuration(Config config) {
        Properties properties = new Properties();
        Streams.stream(config.getPropertyNames())
                .map(propertyName -> new ServerConfig(
                        propertyName, config.getConfigValue(propertyName).getValue()))
                .filter(ServerConfig::isValid)
                .forEach(serverConfig -> properties.put(serverConfig.key(), serverConfig.value()));

        return LHConfig.newBuilder().loadFromProperties(properties).build();
    }

    record ServerConfig(String key, Object value) {
        static final String CONFIG_PREFIX_REGEX = "^(LHC_|LHW_)[A-Z_]+";

        ServerConfig(String key, Object value) {
            this.value = value;
            this.key = key == null ? null : key.strip().toUpperCase().replaceAll("[.-]", "_");
        }

        boolean isValid() {
            if (value == null) return false;
            if (key == null) return false;
            if (!key.matches(CONFIG_PREFIX_REGEX)) return false;
            return LHConfig.configNames().contains(key);
        }
    }
}

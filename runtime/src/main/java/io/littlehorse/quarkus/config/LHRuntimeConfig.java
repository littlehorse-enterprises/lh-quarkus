package io.littlehorse.quarkus.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "quarkus.littlehorse")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface LHRuntimeConfig {
    @WithName("tasks.start.enabled")
    @WithDefault("true")
    boolean tasksStartEnabled();

    @WithName("tasks.register.enabled")
    @WithDefault("true")
    boolean tasksRegisterEnabled();
}

package io.littlehorse.quarkus.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.Map;

@ConfigMapping(prefix = "quarkus.littlehorse")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface LHRuntimeConfig {
    @WithName("tasks.register.enabled")
    @WithDefault("true")
    boolean tasksRegisterEnabled();

    @WithName("tasks.start.enabled")
    @WithDefault("true")
    boolean tasksStartEnabled();

    @WithName("tasks")
    Map<String, TaskConfig> specificTaskConfigs();

    interface TaskConfig {
        @WithName("register.enabled")
        @WithDefault("${quarkus.littlehorse.tasks.register.enabled}")
        Boolean registerEnabled();

        @WithName("start.enabled")
        @WithDefault("${quarkus.littlehorse.tasks.start.enabled}")
        Boolean startEnabled();
    }

    @WithName("workflows.register.enabled")
    @WithDefault("true")
    boolean workflowsRegisterEnabled();

    @WithName("workflows")
    Map<String, WorkflowConfig> specificWorkflowConfigs();

    interface WorkflowConfig {
        @WithName("register.enabled")
        @WithDefault("${quarkus.littlehorse.workflows.register.enabled}")
        Boolean registerEnabled();
    }

    @WithName("user-tasks.register.enabled")
    @WithDefault("true")
    boolean userTaskRegisterEnabled();

    @WithName("user-tasks")
    Map<String, UserTaskConfig> specificUserTaskConfigs();

    interface UserTaskConfig {
        @WithName("register.enabled")
        @WithDefault("${quarkus.littlehorse.user-tasks.register.enabled}")
        Boolean registerEnabled();
    }

    @WithName("structs.register.enabled")
    @WithDefault("true")
    boolean structsRegisterEnabled();

    @WithName("structs")
    Map<String, StructConfig> specificStructConfigs();

    interface StructConfig {
        @WithName("register.enabled")
        @WithDefault("${quarkus.littlehorse.structs.register.enabled}")
        Boolean registerEnabled();

        @WithName("register.compatibility")
        @WithDefault("NO_SCHEMA_UPDATES")
        String compatibility();
    }
}

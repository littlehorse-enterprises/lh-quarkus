package io.littlehorse.quarkus.config;

import io.littlehorse.sdk.common.proto.StructDefCompatibilityType;
import io.littlehorse.sdk.worker.LHTaskWorkerHealthReason;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.List;
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
        boolean registerEnabled();

        @WithName("start.enabled")
        @WithDefault("${quarkus.littlehorse.tasks.start.enabled}")
        boolean startEnabled();
    }

    @WithName("workflows.register.enabled")
    @WithDefault("true")
    boolean workflowsRegisterEnabled();

    @WithName("workflows")
    Map<String, WorkflowConfig> specificWorkflowConfigs();

    interface WorkflowConfig {
        @WithName("register.enabled")
        @WithDefault("${quarkus.littlehorse.workflows.register.enabled}")
        boolean registerEnabled();
    }

    @WithName("user-tasks.register.enabled")
    @WithDefault("true")
    boolean userTaskRegisterEnabled();

    @WithName("user-tasks")
    Map<String, UserTaskConfig> specificUserTaskConfigs();

    interface UserTaskConfig {
        @WithName("register.enabled")
        @WithDefault("${quarkus.littlehorse.user-tasks.register.enabled}")
        boolean registerEnabled();
    }

    @WithName("structs.register.enabled")
    @WithDefault("true")
    boolean structsRegisterEnabled();

    @WithName("structs")
    Map<String, StructConfig> specificStructConfigs();

    interface StructConfig {
        @WithName("register.enabled")
        @WithDefault("${quarkus.littlehorse.structs.register.enabled}")
        boolean registerEnabled();

        @WithName("register.compatibility")
        @WithDefault("NO_SCHEMA_UPDATES")
        StructDefCompatibilityType compatibility();
    }

    @WithName("type-adapters.register.enabled")
    @WithDefault("true")
    boolean typeAdaptersRegisterEnabled();

    @WithName("type-adapters")
    Map<String, TypeAdapterConfig> specificTypeAdapterConfigs();

    interface TypeAdapterConfig {
        @WithName("register.enabled")
        @WithDefault("${quarkus.littlehorse.type-adapters.register.enabled}")
        boolean registerEnabled();
    }

    @WithName("health.statuses")
    @WithDefault("HEALTHY,SERVER_REBALANCING")
    List<LHTaskWorkerHealthReason> healthStatuses();
}

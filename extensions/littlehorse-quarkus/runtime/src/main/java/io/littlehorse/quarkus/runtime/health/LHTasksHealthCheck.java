package io.littlehorse.quarkus.runtime.health;

import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.runtime.LHTaskStatusesContainer;
import io.littlehorse.sdk.worker.LHTaskWorkerHealthReason;
import io.quarkus.arc.lookup.LookupIfProperty;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.util.List;

@Readiness
@ApplicationScoped
@LookupIfProperty(name = "quarkus.littlehorse.tasks.start.enabled", stringValue = "true")
public class LHTasksHealthCheck implements HealthCheck {

    private final LHTaskStatusesContainer taskStatusesContainer;
    private final List<LHTaskWorkerHealthReason> validStatuses;

    public LHTasksHealthCheck(
            LHTaskStatusesContainer taskStatusesContainer, LHRuntimeConfig runtimeConfig) {
        this.taskStatusesContainer = taskStatusesContainer;
        this.validStatuses = runtimeConfig.healthStatuses();
    }

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("LH Tasks").status(isHealthy()).build();
    }

    private boolean isHealthy() {
        try {
            return taskStatusesContainer.getTaskStatuses().stream().allMatch(this::isValid);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValid(LHTaskStatus lhTaskStatus) {
        return validStatuses.contains(lhTaskStatus.getStatus());
    }
}

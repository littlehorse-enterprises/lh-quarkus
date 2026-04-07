package io.littlehorse.quarkus.runtime.health;

import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.runtime.LHTaskStatusesContainer;
import io.quarkus.arc.lookup.LookupIfProperty;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
@LookupIfProperty(name = "quarkus.littlehorse.tasks.start.enabled", stringValue = "true")
public class LHTasksHealthCheck implements HealthCheck {

    private final LHTaskStatusesContainer taskStatusesContainer;
    private final LHRuntimeConfig runtimeConfig;

    public LHTasksHealthCheck(
            LHTaskStatusesContainer taskStatusesContainer, LHRuntimeConfig runtimeConfig) {
        this.taskStatusesContainer = taskStatusesContainer;
        this.runtimeConfig = runtimeConfig;
    }

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("LH Tasks").status(isHealthy()).build();
    }

    private boolean isHealthy() {
        try {
            return taskStatusesContainer.getTaskStatuses().stream()
                    .allMatch(lhTaskStatus ->
                            runtimeConfig.healthStatuses().contains(lhTaskStatus.getReason()));
        } catch (Exception e) {
            return false;
        }
    }
}

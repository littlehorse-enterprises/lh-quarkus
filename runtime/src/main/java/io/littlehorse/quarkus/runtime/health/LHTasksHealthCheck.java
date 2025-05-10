package io.littlehorse.quarkus.runtime.health;

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

    public LHTasksHealthCheck(LHTaskStatusesContainer taskStatusesContainer) {
        this.taskStatusesContainer = taskStatusesContainer;
    }

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("LH Tasks").status(isHealthy()).build();
    }

    private boolean isHealthy() {
        try {
            return taskStatusesContainer.getTaskStatuses().stream()
                    .allMatch(LHTaskStatus::isHealthy);
        } catch (Exception e) {
            return false;
        }
    }
}

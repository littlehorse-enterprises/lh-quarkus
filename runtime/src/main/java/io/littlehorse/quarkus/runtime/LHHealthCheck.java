package io.littlehorse.quarkus.runtime;

import io.littlehorse.sdk.worker.LHTaskWorker;
import io.littlehorse.sdk.worker.LHTaskWorkerHealth;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class LHHealthCheck implements HealthCheck {

    private final LHTaskWorkerContainer workerContainer;

    public LHHealthCheck(LHTaskWorkerContainer workerContainer) {
        this.workerContainer = workerContainer;
    }

    @Override
    public HealthCheckResponse call() {
        boolean healthy = workerContainer.getTaskWorkers().stream()
                .map(LHTaskWorker::healthStatus)
                .allMatch(LHTaskWorkerHealth::isHealthy);
        HealthCheckResponseBuilder statusBuilder =
                HealthCheckResponse.named("LH Task Workers").up();
        if (!healthy) {
            statusBuilder.down();
        }
        return statusBuilder.build();
    }
}

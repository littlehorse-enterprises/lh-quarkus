package io.littlehorse.quarkus.runtime;

import io.littlehorse.sdk.worker.LHTaskWorker;
import io.littlehorse.sdk.worker.LHTaskWorkerHealth;
import io.quarkus.arc.lookup.LookupIfProperty;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
@LookupIfProperty(name = "quarkus.littlehorse.tasks.start.enabled", stringValue = "true")
public class LHTaskWorkersHealthCheck implements HealthCheck {

    private final LHTaskWorkerRegister workerRegister;

    public LHTaskWorkersHealthCheck(LHTaskWorkerRegister workerRegister) {
        this.workerRegister = workerRegister;
    }

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("LH Task Workers").status(isHealthy()).build();
    }

    private boolean isHealthy() {
        try {
            return workerRegister.getTaskWorkers().stream()
                    .map(LHTaskWorker::healthStatus)
                    .allMatch(LHTaskWorkerHealth::isHealthy);
        } catch (Exception e) {
            return false;
        }
    }
}

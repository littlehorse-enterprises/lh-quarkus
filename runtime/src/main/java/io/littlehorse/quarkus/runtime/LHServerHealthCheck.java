package io.littlehorse.quarkus.runtime;

import com.google.protobuf.Empty;

import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class LHServerHealthCheck implements HealthCheck {

    private final LittleHorseBlockingStub blockingStub;

    public LHServerHealthCheck(LittleHorseBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("LH Server").status(isHealthy()).build();
    }

    private boolean isHealthy() {
        try {
            blockingStub.getServerVersion(Empty.getDefaultInstance());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

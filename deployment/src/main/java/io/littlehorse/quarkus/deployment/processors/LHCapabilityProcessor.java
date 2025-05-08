package io.littlehorse.quarkus.deployment.processors;

import io.littlehorse.quarkus.config.LHBuildtimeConfig;
import io.littlehorse.quarkus.runtime.LHHealthCheck;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;

public class LHCapabilityProcessor {

    @BuildStep
    HealthBuildItem addHealthCheck(LHBuildtimeConfig config) {
        return new HealthBuildItem(LHHealthCheck.class.getName(), config.healthEnabled());
    }
}

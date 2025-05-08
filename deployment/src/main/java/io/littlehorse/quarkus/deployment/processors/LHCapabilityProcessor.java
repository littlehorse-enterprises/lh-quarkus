package io.littlehorse.quarkus.deployment.processors;

import io.littlehorse.quarkus.config.LHBuildtimeConfig;
import io.littlehorse.quarkus.runtime.LHServerHealthCheck;
import io.littlehorse.quarkus.runtime.LHTaskWorkersHealthCheck;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;

public class LHCapabilityProcessor {

    @BuildStep
    HealthBuildItem addLHTaskWorkersHealthCheck(LHBuildtimeConfig config) {
        return new HealthBuildItem(
                LHTaskWorkersHealthCheck.class.getName(), config.healthEnabled());
    }

    @BuildStep
    HealthBuildItem addLHServerHealthCheck(LHBuildtimeConfig config) {
        return new HealthBuildItem(LHServerHealthCheck.class.getName(), config.healthEnabled());
    }
}

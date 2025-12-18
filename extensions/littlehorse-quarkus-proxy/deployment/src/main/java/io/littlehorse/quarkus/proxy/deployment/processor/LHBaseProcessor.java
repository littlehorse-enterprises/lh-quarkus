package io.littlehorse.quarkus.proxy.deployment.processor;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class LHBaseProcessor {

    @BuildStep
    FeatureBuildItem produceFeatureName() {
        return new FeatureBuildItem("littlehorse-quarkus-proxy");
    }
}

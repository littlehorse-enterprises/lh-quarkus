package io.littlehorse.quarkus.proxy.deployment.processor;

import io.littlehorse.quarkus.proxy.resource.ServerInformationResource;
import io.littlehorse.quarkus.proxy.service.ServerInformationService;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class LHProxyExtensionProcessor {

    @BuildStep
    FeatureBuildItem produceFeatureName() {
        return new FeatureBuildItem("littlehorse-quarkus-proxy");
    }

    @BuildStep
    AdditionalIndexedClassesBuildItem produceResources() {
        return new AdditionalIndexedClassesBuildItem(ServerInformationResource.class.getName());
    }

    @BuildStep
    AdditionalBeanBuildItem produceBeans() {
        return new AdditionalBeanBuildItem(
                ServerInformationResource.class, ServerInformationService.class);
    }
}

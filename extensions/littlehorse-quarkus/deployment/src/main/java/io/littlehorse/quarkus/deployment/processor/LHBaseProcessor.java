package io.littlehorse.quarkus.deployment.processor;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageConfigBuildItem;

public class LHBaseProcessor {

    @BuildStep
    FeatureBuildItem produceFeatureName() {
        return new FeatureBuildItem("littlehorse-quarkus");
    }

    @BuildStep
    NativeImageConfigBuildItem produceNimbusNativeImageConfiguration() {
        return NativeImageConfigBuildItem.builder()
                .addRuntimeInitializedClass("com.nimbusds.oauth2.sdk.http.HTTPRequest")
                .build();
    }
}

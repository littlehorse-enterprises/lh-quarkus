package io.littlehorse.quarkus.deployment.processors;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;

public class LHDependencyProcessor {

    @BuildStep
    IndexDependencyBuildItem produceLittleHorseClientDependency() {
        return new IndexDependencyBuildItem("io.littlehorse", "littlehorse-client");
    }

    @BuildStep
    IndexDependencyBuildItem produceProtobufDependency() {
        return new IndexDependencyBuildItem("com.google.protobuf", "protobuf-java");
    }
}

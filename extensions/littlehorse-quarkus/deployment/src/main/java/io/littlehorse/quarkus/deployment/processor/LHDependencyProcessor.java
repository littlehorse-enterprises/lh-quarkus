package io.littlehorse.quarkus.deployment.processor;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;

public class LHDependencyProcessor {

    @BuildStep
    IndexDependencyBuildItem produceLittleHorseClientDependency() {
        return new IndexDependencyBuildItem("io.littlehorse", "littlehorse-client");
    }

    @BuildStep
    IndexDependencyBuildItem produceProtobufJavaDependency() {
        return new IndexDependencyBuildItem("com.google.protobuf", "protobuf-java");
    }

    @BuildStep
    IndexDependencyBuildItem produceProtobufJavaUtilDependency() {
        return new IndexDependencyBuildItem("com.google.protobuf", "protobuf-java-util");
    }

    @BuildStep
    IndexDependencyBuildItem produceGrpcProtobufDependency() {
        return new IndexDependencyBuildItem("io.grpc", "grpc-protobuf");
    }

    @BuildStep
    IndexDependencyBuildItem produceGrpcStubDependency() {
        return new IndexDependencyBuildItem("io.grpc", "grpc-stub");
    }
}

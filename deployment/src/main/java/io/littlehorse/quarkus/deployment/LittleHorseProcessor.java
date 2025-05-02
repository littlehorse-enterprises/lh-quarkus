package io.littlehorse.quarkus.deployment;

import com.google.common.collect.Streams;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.ProtocolMessageEnum;

import io.littlehorse.quarkus.runtime.LittleHorseBeans;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageConfigBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

import org.jboss.jandex.IndexView;

import java.util.stream.Stream;

class LittleHorseProcessor {

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("littlehorse-quarkus");
    }

    @BuildStep
    NativeImageConfigBuildItem nativeImageConfiguration() {
        return NativeImageConfigBuildItem.builder()
                .addRuntimeInitializedClass("com.nimbusds.oauth2.sdk.http.HTTPRequest")
                .build();
    }

    @BuildStep
    void registerDependencies(BuildProducer<IndexDependencyBuildItem> indexDependency) {
        Stream.of("io.littlehorse:littlehorse-client", "com.google.protobuf:protobuf-java")
                .map(dependency -> dependency.split(":"))
                .map(dependency -> new IndexDependencyBuildItem(dependency[0], dependency[1]))
                .forEach(indexDependency::produce);
    }

    @BuildStep
    void registerBeans(BuildProducer<AdditionalBeanBuildItem> beans) {
        beans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClasses(LittleHorseBeans.class)
                .build());
    }

    @BuildStep
    void registerForReflection(
            CombinedIndexBuildItem combinedIndex,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        IndexView index = combinedIndex.getIndex();
        Streams.concat(
                        index.getKnownClasses().stream()
                                .filter(classInfo -> classInfo.methods().stream()
                                        .anyMatch(methodInfo ->
                                                methodInfo.hasAnnotation(LHTaskMethod.class))),
                        index.getAllKnownSubclasses(GeneratedMessageV3.class).stream(),
                        index.getAllKnownSubclasses(GeneratedMessageV3.Builder.class).stream(),
                        index.getAllKnownImplementations(ProtocolMessageEnum.class).stream())
                .map(classInfo -> classInfo.name().toString())
                .distinct()
                .map(className -> ReflectiveClassBuildItem.builder(className)
                        .methods()
                        .fields()
                        .build())
                .forEach(reflectiveClasses::produce);
    }
}

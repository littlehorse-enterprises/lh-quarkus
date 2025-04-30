package io.littlehorse.quarkus.extension.poc.deployment;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.ProtocolMessageEnum;
import io.littlehorse.quarkus.extension.poc.runtime.LittleHorseBeans;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageConfigBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

class LittleHorseProcessor {

    public static final List<Dependency> DEPENDENCIES = List.of(
            new Dependency("io.littlehorse", "littlehorse-client"),
            new Dependency("com.google.protobuf", "protobuf-java")
    );
    private static final String FEATURE = "littlehorse-quarkus";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    NativeImageConfigBuildItem nativeImageConfiguration() {
        return NativeImageConfigBuildItem.builder()
                .addRuntimeInitializedClass("com.nimbusds.oauth2.sdk.http.HTTPRequest").build();
    }

    // https://quarkus.io/guides/writing-extensions#adding-external-jars-to-the-indexer-with-indexdependencybuilditem
    @BuildStep
    void registerDependencies(BuildProducer<IndexDependencyBuildItem> indexDependency) {
        DEPENDENCIES.stream()
                .map(dependency -> new IndexDependencyBuildItem(dependency.groupId(), dependency.artifactId()))
                .forEach(indexDependency::produce);
    }

    //https://quarkus.io/guides/writing-native-applications-tips#register-reflection
    @BuildStep
    void registerForReflection(CombinedIndexBuildItem combinedIndex, BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        IndexView index = combinedIndex.getIndex();
        registerForReflection(reflectiveClasses, index.getKnownClasses(), classInfo -> classInfo.methods().stream().anyMatch(methodInfo -> methodInfo.hasAnnotation(LHTaskMethod.class)));
        registerForReflection(reflectiveClasses, index.getAllKnownSubclasses(GeneratedMessageV3.class));
        registerForReflection(reflectiveClasses, index.getAllKnownSubclasses(GeneratedMessageV3.Builder.class));
        registerForReflection(reflectiveClasses, index.getAllKnownImplementors(ProtocolMessageEnum.class));
    }

    @BuildStep
    void registerBeans(BuildProducer<AdditionalBeanBuildItem> beans) {
        beans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClasses(LittleHorseBeans.class)
                .build());
    }

    void registerForReflection(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses, Collection<ClassInfo> classes) {
        registerForReflection(reflectiveClasses, classes, classInfo -> true);
    }

    void registerForReflection(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses, Collection<ClassInfo> classes, Predicate<ClassInfo> filter) {
        classes.stream()
                .filter(filter)
                .map(classInfo -> classInfo.name().toString())
                .map(className -> ReflectiveClassBuildItem.builder(className).methods().fields().build())
                .forEach(reflectiveClasses::produce);
    }

    record Dependency(String groupId, String artifactId) {
    }
}

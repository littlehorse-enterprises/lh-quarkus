package io.littlehorse.quarkus.deployment.processor;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.usertask.annotations.UserTaskField;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.MethodInfo;

import java.util.function.Function;

public class LHReflectionProcessor {

    private static final Function<String, ReflectiveClassBuildItem> newBuildItem = className ->
            ReflectiveClassBuildItem.builder(className).methods().fields().build();

    @BuildStep
    void registerLHWorkflow(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHWorkflow.class).stream()
                .map(AnnotationInstance::target)
                .filter(target -> target.kind().equals(AnnotationTarget.Kind.METHOD))
                .map(AnnotationTarget::asMethod)
                .map(MethodInfo::declaringClass)
                .map(ClassInfo::toString)
                .distinct()
                .map(newBuildItem)
                .forEach(producer::produce);
    }

    @BuildStep
    void registerUserTaskField(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getKnownClasses().stream()
                .filter(classInfo -> classInfo.fields().stream()
                        .anyMatch(fieldInfo -> fieldInfo.hasAnnotation(UserTaskField.class)))
                .map(ClassInfo::toString)
                .distinct()
                .map(newBuildItem)
                .forEach(producer::produce);
    }

    @BuildStep
    void registerLHUserTaskFrom(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHUserTaskForm.class).stream()
                .map(AnnotationInstance::target)
                .map(AnnotationTarget::asClass)
                .map(ClassInfo::toString)
                .map(newBuildItem)
                .forEach(producer::produce);
    }

    @BuildStep
    void registerLHTaskMethod(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getKnownClasses().stream()
                .filter(classInfo -> classInfo.methods().stream()
                        .anyMatch(methodInfo -> methodInfo.hasAnnotation(LHTaskMethod.class)))
                .map(ClassInfo::toString)
                .distinct()
                .map(newBuildItem)
                .forEach(producer::produce);
    }

    @BuildStep
    void registerLHTask(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHTask.class).stream()
                .map(AnnotationInstance::target)
                .map(AnnotationTarget::asClass)
                .map(ClassInfo::toString)
                .map(newBuildItem)
                .forEach(producer::produce);
    }
}

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
import org.jboss.jandex.ArrayType;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

import java.util.List;
import java.util.function.Function;

public class LHReflectionProcessor {

    private static final Function<String, ReflectiveClassBuildItem> newBuildItem = className ->
            ReflectiveClassBuildItem.builder(className).methods().fields().build();
    public static final DotName JAVA_LANG = DotName.createSimple("java.lang");

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

    @BuildStep
    void registerReturnedClassForLHTaskMethod(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getKnownClasses().stream()
                .filter(classInfo -> classInfo.methods().stream()
                        .anyMatch(methodInfo -> methodInfo.hasAnnotation(LHTaskMethod.class)))
                .flatMap(classInfo -> classInfo.methods().stream())
                .map(MethodInfo::returnType)
                .filter(type -> type.kind().equals(Type.Kind.CLASS))
                .map(Type::asClassType)
                .map(Type::name)
                .filter(dotName -> !dotName.prefix().equals(JAVA_LANG))
                .map(DotName::toString)
                .distinct()
                .map(newBuildItem)
                .forEach(producer::produce);
    }

    @BuildStep
    void registerReturnedArrayForLHTaskMethod(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getKnownClasses().stream()
                .filter(classInfo -> classInfo.methods().stream()
                        .anyMatch(methodInfo -> methodInfo.hasAnnotation(LHTaskMethod.class)))
                .flatMap(classInfo -> classInfo.methods().stream())
                .map(MethodInfo::returnType)
                .filter(type -> type.kind().equals(Type.Kind.ARRAY))
                .map(Type::asArrayType)
                .map(ArrayType::elementType)
                .map(Type::name)
                .filter(dotName -> !dotName.prefix().equals(JAVA_LANG))
                .map(DotName::toString)
                .distinct()
                .map(newBuildItem)
                .forEach(producer::produce);
    }

    @BuildStep
    void registerReturnedListForLHTaskMethod(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getKnownClasses().stream()
                .filter(classInfo -> classInfo.methods().stream()
                        .anyMatch(methodInfo -> methodInfo.hasAnnotation(LHTaskMethod.class)))
                .flatMap(classInfo -> classInfo.methods().stream())
                .map(MethodInfo::returnType)
                .filter(type -> type.kind().equals(Type.Kind.PARAMETERIZED_TYPE))
                .map(Type::asParameterizedType)
                .filter(parameterizedType ->
                        parameterizedType.name().equals(DotName.createSimple(List.class)))
                .flatMap(parameterizedType -> parameterizedType.arguments().stream())
                .map(Type::name)
                .filter(dotName -> !dotName.prefix().equals(JAVA_LANG))
                .map(DotName::toString)
                .distinct()
                .map(newBuildItem)
                .forEach(producer::produce);
    }
}

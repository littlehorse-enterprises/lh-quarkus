package io.littlehorse.quarkus.deployment.processor;

import static org.jboss.jandex.AnnotationTarget.Kind.METHOD;
import static org.jboss.jandex.Type.Kind.ARRAY;
import static org.jboss.jandex.Type.Kind.CLASS;
import static org.jboss.jandex.Type.Kind.PARAMETERIZED_TYPE;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.ProtocolMessageEnum;

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
    private static final DotName JAVA_LANG = DotName.createSimple("java.lang");
    private static final DotName JAVA_UTIL_LIST = DotName.createSimple(List.class);
    private static final DotName GRPC_MESSAGE = DotName.createSimple(GeneratedMessage.class);
    private static final DotName GRPC_MESSAGE_BUILDER =
            DotName.createSimple(GeneratedMessage.Builder.class);
    private static final DotName GRPC_MESSAGE_ENUM =
            DotName.createSimple(ProtocolMessageEnum.class);

    @BuildStep
    void registerGeneratedMessage(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAllKnownSubclasses(GRPC_MESSAGE).stream()
                .map(ClassInfo::toString)
                .map(newBuildItem)
                .forEach(producer::produce);
    }

    @BuildStep
    void registerGeneratedMessageBuilder(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAllKnownSubclasses(GRPC_MESSAGE_BUILDER).stream()
                .map(ClassInfo::toString)
                .map(newBuildItem)
                .forEach(producer::produce);
    }

    @BuildStep
    void registerProtocolMessageEnum(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAllKnownImplementations(GRPC_MESSAGE_ENUM).stream()
                .map(ClassInfo::toString)
                .map(newBuildItem)
                .forEach(producer::produce);
    }

    @BuildStep
    void registerLHWorkflow(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHWorkflow.class).stream()
                .map(AnnotationInstance::target)
                .filter(target -> target.kind().equals(METHOD))
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
                .filter(type -> type.kind().equals(CLASS))
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
                .filter(type -> type.kind().equals(ARRAY))
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
                .filter(type -> type.kind().equals(PARAMETERIZED_TYPE))
                .map(Type::asParameterizedType)
                .filter(parameterizedType -> parameterizedType.name().equals(JAVA_UTIL_LIST))
                .flatMap(parameterizedType -> parameterizedType.arguments().stream())
                .map(Type::name)
                .filter(dotName -> !dotName.prefix().equals(JAVA_LANG))
                .map(DotName::toString)
                .distinct()
                .map(newBuildItem)
                .forEach(producer::produce);
    }
}

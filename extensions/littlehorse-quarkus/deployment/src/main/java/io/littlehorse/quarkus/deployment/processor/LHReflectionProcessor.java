package io.littlehorse.quarkus.deployment.processor;

import static org.jboss.jandex.AnnotationTarget.Kind.METHOD;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.usertask.annotations.UserTaskField;
import io.littlehorse.sdk.worker.LHStructDef;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class LHReflectionProcessor {

    private static final DotName JAVA_PACKAGE = DotName.createSimple("java");
    private static final DotName LITTLEHORSE_SDK_PACKAGE =
            DotName.createSimple("io.littlehorse.sdk");

    private static final Function<String, ReflectiveClassBuildItem> newBuildItem = className ->
            ReflectiveClassBuildItem.builder(className).methods().fields().build();

    @BuildStep
    void registerJgraphtEdge(BuildProducer<ReflectiveClassBuildItem> producer) {
        // LHRecordableDependenciesGraph builds a DefaultDirectedGraph whose edge supplier
        // instantiates DefaultEdge reflectively, so its constructor must be registered for native.
        producer.produce(ReflectiveClassBuildItem.builder("org.jgrapht.graph.DefaultEdge")
                .constructors()
                .build());
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
    void registerLHUserTaskForm(
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
    void registerLHTypes(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        reflectiveTypeNames(indexContainer.getIndex()).stream()
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

    static Set<String> reflectiveTypeNames(IndexView index) {
        Set<DotName> typeNames = new LinkedHashSet<>();
        Set<DotName> visited = new LinkedHashSet<>();

        index.getAnnotations(LHTaskMethod.class).stream()
                .map(AnnotationInstance::target)
                .filter(target -> target.kind().equals(METHOD))
                .map(AnnotationTarget::asMethod)
                .forEach(method -> {
                    collectType(method.returnType(), index, typeNames, visited);
                    method.parameterTypes()
                            .forEach(type -> collectType(type, index, typeNames, visited));
                });

        index.getAnnotations(LHStructDef.class).stream()
                .map(AnnotationInstance::target)
                .map(AnnotationTarget::asClass)
                .map(ClassInfo::name)
                .forEach(name -> collectClass(name, index, typeNames, visited));

        return typeNames.stream()
                .map(DotName::toString)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    private static void collectType(
            Type type, IndexView index, Set<DotName> typeNames, Set<DotName> visited) {
        switch (type.kind()) {
            case ARRAY -> collectType(type.asArrayType().elementType(), index, typeNames, visited);
            case CLASS -> collectClass(type.name(), index, typeNames, visited);
            case PARAMETERIZED_TYPE -> {
                collectClass(type.name(), index, typeNames, visited);
                type.asParameterizedType()
                        .arguments()
                        .forEach(argument -> collectType(argument, index, typeNames, visited));
            }
            default -> {}
        }
    }

    private static void collectClass(
            DotName name, IndexView index, Set<DotName> typeNames, Set<DotName> visited) {
                if (name.startsWith(JAVA_PACKAGE) || !visited.add(name)) {
            return;
        }

        typeNames.add(name);
                if (name.startsWith(LITTLEHORSE_SDK_PACKAGE)) {
            return;
        }

        ClassInfo classInfo = index.getClassByName(name);
        if (classInfo == null || classInfo.isInterface()) {
            return;
        }

        classInfo.methods().stream()
                .filter(method -> !Modifier.isStatic(method.flags()))
                .filter(method -> method.parametersCount() == 0)
                .filter(LHReflectionProcessor::isBeanGetter)
                .map(MethodInfo::returnType)
                .forEach(type -> collectType(type, index, typeNames, visited));
    }

    private static boolean isBeanGetter(MethodInfo method) {
        return (method.name().startsWith("get") && method.name().length() > 3)
                || (method.name().startsWith("is") && method.name().length() > 2);
    }
}

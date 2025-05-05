package io.littlehorse.quarkus.deployment;

import com.google.common.collect.Streams;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.ProtocolMessageEnum;

import io.littlehorse.quarkus.runtime.LHBeans;
import io.littlehorse.quarkus.runtime.LHRecorder;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.usertask.annotations.UserTaskField;
import io.littlehorse.sdk.wfsdk.ThreadFunc;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.builder.item.MultiBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageConfigBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

import jakarta.inject.Singleton;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

class LHProcessor {

    static final DotName SINGLETON_SCOPE = DotName.createSimple(Singleton.class);
    static final DotName LH_WORKFLOW_ANNOTATION = DotName.createSimple(LHWorkflow.class);
    static final DotName LH_TASK_ANNOTATION = DotName.createSimple(LHTask.class);
    static final DotName LH_TASK_METHOD_ANNOTATION = DotName.createSimple(LHTaskMethod.class);
    static final DotName LH_USER_TASK_FORM_ANNOTATION = DotName.createSimple(LHUserTaskForm.class);
    static final DotName LH_USER_TASK_FIELD_ANNOTATION = DotName.createSimple(UserTaskField.class);
    static final DotName THREAD_FUNC_INTERFACE = DotName.createSimple(ThreadFunc.class);

    static Class<?> loadClass(ClassInfo classInfo) {
        try {
            return Thread.currentThread()
                    .getContextClassLoader()
                    .loadClass(classInfo.name().toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

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
    void registerDependencies(BuildProducer<IndexDependencyBuildItem> producer) {
        Stream.of("io.littlehorse:littlehorse-client", "com.google.protobuf:protobuf-java")
                .map(dependency -> dependency.split(":"))
                .map(dependency -> new IndexDependencyBuildItem(dependency[0], dependency[1]))
                .forEach(producer::produce);
    }

    @BuildStep
    void registerAdditionalBeans(BuildProducer<AdditionalBeanBuildItem> producer) {
        producer.produce(
                AdditionalBeanBuildItem.builder().addBeanClasses(LHBeans.class).build());
    }

    @BuildStep
    void registerForReflection(
            BuildProducer<ReflectiveClassBuildItem> producer,
            CombinedIndexBuildItem indexContainer) {
        IndexView index = indexContainer.getIndex();

        Stream<ClassInfo> tasks = index.getAnnotations(LH_TASK_ANNOTATION).stream()
                .map(annotationInstance -> annotationInstance.target().asClass());

        Stream<ClassInfo> taskMethods = index.getKnownClasses().stream()
                .filter(classInfo -> classInfo.methods().stream()
                        .anyMatch(
                                methodInfo -> methodInfo.hasAnnotation(LH_TASK_METHOD_ANNOTATION)));

        Stream<ClassInfo> userTaskForms =
                index.getAnnotations(LH_USER_TASK_FORM_ANNOTATION).stream()
                        .map(annotationInstance -> annotationInstance.target().asClass());

        Stream<ClassInfo> userTaskFields = index.getKnownClasses().stream()
                .filter(classInfo -> classInfo.fields().stream()
                        .anyMatch(fieldInfo ->
                                fieldInfo.hasAnnotation(LH_USER_TASK_FIELD_ANNOTATION)));

        Stream<ClassInfo> protobufMessages =
                index.getAllKnownSubclasses(GeneratedMessageV3.class).stream();

        Stream<ClassInfo> protobufBuilders =
                index.getAllKnownSubclasses(GeneratedMessageV3.Builder.class).stream();

        Stream<ClassInfo> protobufEnums =
                index.getAllKnownImplementations(ProtocolMessageEnum.class).stream();

        Streams.concat(
                        tasks,
                        taskMethods,
                        userTaskForms,
                        userTaskFields,
                        protobufMessages,
                        protobufBuilders,
                        protobufEnums)
                .map(classInfo -> classInfo.name().toString())
                .distinct()
                .map(className -> ReflectiveClassBuildItem.builder(className)
                        .methods()
                        .fields()
                        .build())
                .forEach(producer::produce);
    }

    @BuildStep
    void registerAnnotations(BuildProducer<BeanDefiningAnnotationBuildItem> producer) {
        Stream.of(LH_TASK_ANNOTATION, LH_WORKFLOW_ANNOTATION, LH_USER_TASK_FORM_ANNOTATION)
                .map(dotName -> new BeanDefiningAnnotationBuildItem(dotName, SINGLETON_SCOPE))
                .forEach(producer::produce);
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void scanLHTasks(
            LHRecorder recorder,
            BuildProducer<LHTaskMethodBuildItem> producer,
            BeanArchiveIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LH_TASK_ANNOTATION).stream()
                .map(annotated -> annotated.target().asClass())
                .filter(classInfo -> classInfo.methods().stream()
                        .anyMatch(
                                methodInfo -> methodInfo.hasAnnotation(LH_TASK_METHOD_ANNOTATION)))
                .map(LHProcessor::loadClass)
                .forEach(clazz -> Arrays.stream(clazz.getMethods())
                        .filter(method -> method.isAnnotationPresent(LHTaskMethod.class))
                        .map(method -> method.getAnnotation(LHTaskMethod.class).value())
                        .map(name -> new LHTaskMethodBuildItem(name, clazz))
                        .forEach(producer::produce));
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void scanLHWorkflow(
            LHRecorder recorder,
            BuildProducer<LHWorkflowBuildItem> producer,
            BeanArchiveIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LH_WORKFLOW_ANNOTATION).stream()
                .filter(annotated -> annotated
                        .target()
                        .asClass()
                        .interfaceNames()
                        .contains(THREAD_FUNC_INTERFACE))
                .map(annotated -> new LHWorkflowBuildItem(
                        annotated.value().asString(),
                        loadClass(annotated.target().asClass())))
                .forEach(producer::produce);
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void scanLHUserTaskForm(
            LHRecorder recorder,
            BuildProducer<LHUserTaskFormBuildItem> producer,
            BeanArchiveIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LH_USER_TASK_FORM_ANNOTATION).stream()
                .map(annotated -> new LHUserTaskFormBuildItem(
                        annotated.value().asString(),
                        loadClass(annotated.target().asClass())))
                .forEach(producer::produce);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    ServiceStartBuildItem startLH(
            LHRecorder recorder,
            ShutdownContextBuildItem shutdownContext,
            List<LHTaskMethodBuildItem> workerBuildItems,
            List<LHUserTaskFormBuildItem> userTaskFromBuildItems,
            List<LHWorkflowBuildItem> workflowBuildItems) {

        workerBuildItems.forEach(buildItem ->
                recorder.startLHTaskMethod(buildItem.name, buildItem.clazz, shutdownContext));

        userTaskFromBuildItems.forEach(
                buildItem -> recorder.registerLHUserTaskForm(buildItem.name, buildItem.clazz));

        workflowBuildItems.forEach(
                buildItem -> recorder.registerLHWorkflow(buildItem.name, buildItem.clazz));

        return new ServiceStartBuildItem("LittleHorse");
    }

    abstract static class LHBuildItem extends MultiBuildItem {
        final String name;
        final Class<?> clazz;

        LHBuildItem(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }
    }

    static final class LHTaskMethodBuildItem extends LHBuildItem {
        LHTaskMethodBuildItem(String name, Class<?> clazz) {
            super(name, clazz);
        }
    }

    static final class LHWorkflowBuildItem extends LHBuildItem {
        LHWorkflowBuildItem(String name, Class<?> clazz) {
            super(name, clazz);
        }
    }

    static final class LHUserTaskFormBuildItem extends LHBuildItem {
        LHUserTaskFormBuildItem(String name, Class<?> clazz) {
            super(name, clazz);
        }
    }
}

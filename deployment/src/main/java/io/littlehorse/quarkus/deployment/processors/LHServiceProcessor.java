package io.littlehorse.quarkus.deployment.processors;

import io.littlehorse.quarkus.deployment.items.LHTaskMethodBuildItem;
import io.littlehorse.quarkus.deployment.items.LHUserTaskFormBuildItem;
import io.littlehorse.quarkus.deployment.items.LHWorkflowConsumerBuildItem;
import io.littlehorse.quarkus.deployment.items.LHWorkflowFromMethodBuildItem;
import io.littlehorse.quarkus.runtime.LHRecorder;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowConsumer;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;

import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.MethodInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class LHServiceProcessor {

    @BuildStep
    void scanLHTasks(
            BuildProducer<LHTaskMethodBuildItem> producer,
            BeanArchiveIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHTask.class).stream()
                .map(annotated -> annotated.target().asClass())
                .filter(classInfo -> classInfo.methods().stream()
                        .anyMatch(methodInfo -> methodInfo.hasAnnotation(LHTaskMethod.class)))
                .map(ClassInfo::toString)
                .map(className -> LHClassLoader.load(className).getLoadedClass())
                .forEach(clazz -> Arrays.stream(clazz.getMethods())
                        .filter(method -> method.isAnnotationPresent(LHTaskMethod.class))
                        .map(method -> method.getAnnotation(LHTaskMethod.class))
                        .filter(Objects::nonNull)
                        .map(LHTaskMethod::value)
                        .map(name -> new LHTaskMethodBuildItem(clazz, name))
                        .forEach(producer::produce));
    }

    @BuildStep
    void scanLHWorkflowConsumerClasses(
            BuildProducer<LHWorkflowConsumerBuildItem> producer,
            BeanArchiveIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHWorkflow.class).stream()
                .filter(annotated -> annotated.target().kind().equals(Kind.CLASS))
                .filter(annotated -> annotated
                        .target()
                        .asClass()
                        .interfaceNames()
                        .contains(DotName.createSimple(LHWorkflowConsumer.class)))
                .map(annotated -> new LHWorkflowConsumerBuildItem(
                        LHClassLoader.load(annotated.target().asClass().toString())
                                .getLoadedClass(),
                        annotated.value().asString()))
                .forEach(producer::produce);
    }

    @BuildStep
    void scanLHWorkflow(
            BuildProducer<LHWorkflowFromMethodBuildItem> producer,
            BeanArchiveIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHWorkflow.class).stream()
                .filter(annotated -> annotated.target().kind().equals(Kind.METHOD))
                .map(annotated -> {
                    MethodInfo methodInfo = annotated.target().asMethod();
                    String className = methodInfo.declaringClass().toString();
                    //                    LHClassLoader classLoader = LHClassLoader.load(className);
                    //                    Class<?> beanClass = classLoader.getLoadedClass();
                    //                    Method method = classLoader.loadMethod(methodInfo.name(),
                    // WorkflowThread.class);
                    String workflowName = annotated.value().asString();
                    return new LHWorkflowFromMethodBuildItem(
                            className, methodInfo.name(), workflowName);
                })
                .forEach(producer::produce);
    }

    @BuildStep
    void scanLHUserTaskForm(
            BuildProducer<LHUserTaskFormBuildItem> producer,
            BeanArchiveIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHUserTaskForm.class).stream()
                .map(annotated -> new LHUserTaskFormBuildItem(
                        LHClassLoader.load(annotated.target().asClass().toString())
                                .getLoadedClass(),
                        annotated.value().asString()))
                .forEach(producer::produce);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    ServiceStartBuildItem startLH(
            LHRecorder recorder,
            ShutdownContextBuildItem shutdownContext,
            List<LHTaskMethodBuildItem> taskBuildItems,
            List<LHUserTaskFormBuildItem> userTaskFromBuildItems,
            List<LHWorkflowConsumerBuildItem> workflowBuildItems,
            List<LHWorkflowFromMethodBuildItem> workflowMethodsBuildItems) {

        taskBuildItems.forEach(buildItem -> recorder.startLHTaskMethod(
                buildItem.getBeanClass(), buildItem.getName(), shutdownContext));

        userTaskFromBuildItems.forEach(buildItem ->
                recorder.registerLHUserTaskForm(buildItem.getBeanClass(), buildItem.getName()));

        workflowBuildItems.forEach(buildItem ->
                recorder.registerLHWorkflow(buildItem.getBeanClass(), buildItem.getName()));

        workflowMethodsBuildItems.stream()
                .map(LHWorkflowFromMethodBuildItem::toRecordable)
                .forEach(recorder::registerLHWorkflowFromMethod);

        return new ServiceStartBuildItem("LittleHorse");
    }
}

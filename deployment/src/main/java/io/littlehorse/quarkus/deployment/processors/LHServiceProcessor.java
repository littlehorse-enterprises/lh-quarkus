package io.littlehorse.quarkus.deployment.processors;

import io.littlehorse.quarkus.deployment.items.LHTaskMethodBuildItem;
import io.littlehorse.quarkus.deployment.items.LHUserTaskFormBuildItem;
import io.littlehorse.quarkus.deployment.items.LHWorkflowDefinitionBuildItem;
import io.littlehorse.quarkus.deployment.items.LHWorkflowFromMethodBuildItem;
import io.littlehorse.quarkus.runtime.LHRecorder;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
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

    private static Class<?> loadClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @BuildStep
    void scanLHTaskMethod(
            BuildProducer<LHTaskMethodBuildItem> producer,
            BeanArchiveIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHTask.class).stream()
                .map(annotated -> annotated.target().asClass())
                .filter(classInfo -> classInfo.methods().stream()
                        .anyMatch(methodInfo -> methodInfo.hasAnnotation(LHTaskMethod.class)))
                .map(ClassInfo::toString)
                .map(LHServiceProcessor::loadClass)
                .forEach(beanClass -> Arrays.stream(beanClass.getMethods())
                        .filter(method -> method.isAnnotationPresent(LHTaskMethod.class))
                        .map(method -> method.getAnnotation(LHTaskMethod.class))
                        .filter(Objects::nonNull)
                        .map(LHTaskMethod::value)
                        .map(taskDefName -> new LHTaskMethodBuildItem(beanClass, taskDefName))
                        .forEach(producer::produce));
    }

    @BuildStep
    void scanLHWorkflowDefinition(
            BuildProducer<LHWorkflowDefinitionBuildItem> producer,
            BeanArchiveIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHWorkflow.class).stream()
                .filter(annotated -> annotated.target().kind().equals(Kind.CLASS))
                .filter(annotated -> annotated
                        .target()
                        .asClass()
                        .interfaceNames()
                        .contains(DotName.createSimple(LHWorkflowDefinition.class)))
                .map(annotated -> {
                    String beanClassName = annotated.target().asClass().toString();
                    String wfSpecName = annotated.value().asString();
                    Class<?> beanClass = loadClass(beanClassName);
                    return new LHWorkflowDefinitionBuildItem(beanClass, wfSpecName);
                })
                .forEach(producer::produce);
    }

    @BuildStep
    void scanLHWorkflowFromMethod(
            BuildProducer<LHWorkflowFromMethodBuildItem> producer,
            BeanArchiveIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHWorkflow.class).stream()
                .filter(annotated -> annotated.target().kind().equals(Kind.METHOD))
                .map(annotated -> {
                    MethodInfo methodInfo = annotated.target().asMethod();
                    String beanClassName = methodInfo.declaringClass().toString();
                    String beanMethodName = methodInfo.name();
                    String wfSpecName = annotated.value().asString();
                    Class<?> beanClass = loadClass(beanClassName);
                    return new LHWorkflowFromMethodBuildItem(beanClass, beanMethodName, wfSpecName);
                })
                .forEach(producer::produce);
    }

    @BuildStep
    void scanLHUserTaskForm(
            BuildProducer<LHUserTaskFormBuildItem> producer,
            BeanArchiveIndexBuildItem indexContainer) {
        indexContainer.getIndex().getAnnotations(LHUserTaskForm.class).stream()
                .map(annotated -> {
                    String beanClassName = annotated.target().asClass().toString();
                    String userTaskDefName = annotated.value().asString();
                    Class<?> beanClass = loadClass(beanClassName);
                    return new LHUserTaskFormBuildItem(beanClass, userTaskDefName);
                })
                .forEach(producer::produce);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    ServiceStartBuildItem startLittleHorseService(
            LHRecorder recorder,
            ShutdownContextBuildItem shutdownContext,
            List<LHTaskMethodBuildItem> taskMethodBuildItems,
            List<LHUserTaskFormBuildItem> userTaskFromBuildItems,
            List<LHWorkflowDefinitionBuildItem> workflowDefinitionBuildItems,
            List<LHWorkflowFromMethodBuildItem> workflowFromMethodBuildItems) {

        taskMethodBuildItems.stream()
                .map(LHTaskMethodBuildItem::toRecordable)
                .forEach(recordable -> recorder.registerAndStartTask(recordable, shutdownContext));

        userTaskFromBuildItems.stream()
                .map(LHUserTaskFormBuildItem::toRecordable)
                .forEach(recorder::registerLHUserTaskForm);

        workflowDefinitionBuildItems.stream()
                .map(LHWorkflowDefinitionBuildItem::toRecordable)
                .forEach(recorder::registerLHWorkflow);

        workflowFromMethodBuildItems.stream()
                .map(LHWorkflowFromMethodBuildItem::toRecordable)
                .forEach(recorder::registerLHWorkflow);

        return new ServiceStartBuildItem("LittleHorse");
    }
}

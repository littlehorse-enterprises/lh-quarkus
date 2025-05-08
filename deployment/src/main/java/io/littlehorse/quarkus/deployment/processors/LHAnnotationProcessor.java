package io.littlehorse.quarkus.deployment.processors;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.deployment.annotations.BuildStep;

import jakarta.inject.Singleton;

import org.jboss.jandex.DotName;

public class LHAnnotationProcessor {

    private static final DotName SINGLETON_ANNOTATION = DotName.createSimple(Singleton.class);

    @BuildStep
    BeanDefiningAnnotationBuildItem produceLHTask() {
        return new BeanDefiningAnnotationBuildItem(
                DotName.createSimple(LHTask.class), SINGLETON_ANNOTATION);
    }

    @BuildStep
    BeanDefiningAnnotationBuildItem produceLHWorkflow() {
        return new BeanDefiningAnnotationBuildItem(
                DotName.createSimple(LHWorkflow.class), SINGLETON_ANNOTATION);
    }

    @BuildStep
    BeanDefiningAnnotationBuildItem produceLHUserTaskForm() {
        return new BeanDefiningAnnotationBuildItem(
                DotName.createSimple(LHUserTaskForm.class), SINGLETON_ANNOTATION);
    }
}

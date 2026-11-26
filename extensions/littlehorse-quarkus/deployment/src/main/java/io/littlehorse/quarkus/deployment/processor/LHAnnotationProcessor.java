package io.littlehorse.quarkus.deployment.processor;

import io.littlehorse.quarkus.adapter.LHTypeAdapter;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.worker.LHStructDef;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.deployment.annotations.BuildStep;

import jakarta.enterprise.inject.Vetoed;
import jakarta.inject.Singleton;

import org.jboss.jandex.AnnotationTransformation;
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
    BeanDefiningAnnotationBuildItem produceLHStructDef() {
        return new BeanDefiningAnnotationBuildItem(
                DotName.createSimple(LHStructDef.class), SINGLETON_ANNOTATION);
    }

    /**
     * Keeps record StructDefs out of CDI.
     *
     * <p>Regular StructDef classes are CDI beans, but records are only data holders. Quarkus would
     * otherwise try to inject every value in a record's constructor and fail to start the
     * application. Marking only records as {@link Vetoed} avoids that problem. The original {@link
     * LHStructDef} annotation stays in the index, so LittleHorse can still register the StructDef
     * and set up reflection for it.
     */
    @BuildStep
    AnnotationsTransformerBuildItem vetoLHStructDefRecords() {
        return new AnnotationsTransformerBuildItem(AnnotationTransformation.forClasses()
                .whenAnyMatch(LHStructDef.class)
                .whenClass(classInfo -> classInfo.isRecord())
                .transform(context -> context.add(Vetoed.class)));
    }

    @BuildStep
    BeanDefiningAnnotationBuildItem produceLHUserTaskForm() {
        return new BeanDefiningAnnotationBuildItem(
                DotName.createSimple(LHUserTaskForm.class), SINGLETON_ANNOTATION);
    }

    @BuildStep
    BeanDefiningAnnotationBuildItem produceLHTypeAdapter() {
        return new BeanDefiningAnnotationBuildItem(
                DotName.createSimple(LHTypeAdapter.class), SINGLETON_ANNOTATION);
    }
}

package io.littlehorse.quarkus.deployment;

import io.littlehorse.quarkus.runtime.LHTaskRecorder;
import io.littlehorse.quarkus.task.LHTask;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.jandex.DotName;

public class LHTaskProcessor {

    @BuildStep
    BeanDefiningAnnotationBuildItem registerAnnotationBean() {
        return new BeanDefiningAnnotationBuildItem(
                DotName.createSimple(LHTask.class), DotName.createSimple(ApplicationScoped.class));
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void scanForBeans(
            LHTaskRecorder recorder,
            BeanArchiveIndexBuildItem beanArchiveIndex,
            BuildProducer<LHTaskBuildItem> beanProducer) {
        System.out.println("HERE");
        beanArchiveIndex
                .getIndex()
                .getAnnotations(LHTask.class)
                .forEach(annotationInstance ->
                        System.out.println(annotationInstance.target().asClass().toString()));
    }
}

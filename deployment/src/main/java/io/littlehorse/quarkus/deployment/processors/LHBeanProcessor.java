package io.littlehorse.quarkus.deployment.processors;

import io.littlehorse.quarkus.runtime.LHExternalBeans;
import io.littlehorse.quarkus.runtime.LHUserTaskRegister;
import io.littlehorse.quarkus.runtime.LHWorkflowRegister;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;

public class LHBeanProcessor {

    @BuildStep
    AdditionalBeanBuildItem produceLHExternalBeans() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(LHExternalBeans.class)
                .build();
    }

    @BuildStep
    AdditionalBeanBuildItem produceLHWorkflowRegister() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(LHWorkflowRegister.class)
                .build();
    }

    @BuildStep
    AdditionalBeanBuildItem produceLHUserTaskRegister() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(LHUserTaskRegister.class)
                .build();
    }
}

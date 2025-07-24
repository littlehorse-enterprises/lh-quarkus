package io.littlehorse.quarkus.deployment.processors;

import io.littlehorse.quarkus.reactive.LittleHorseReactiveStub;
import io.littlehorse.quarkus.runtime.LHExternalBeans;
import io.littlehorse.quarkus.runtime.LHTaskStatusesContainer;
import io.littlehorse.quarkus.runtime.register.LHTaskRegister;
import io.littlehorse.quarkus.runtime.register.LHUserTaskRegister;
import io.littlehorse.quarkus.runtime.register.LHWorkflowRegister;
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

    @BuildStep
    AdditionalBeanBuildItem produceLHTaskRegister() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(LHTaskRegister.class)
                .build();
    }

    @BuildStep
    AdditionalBeanBuildItem produceLHTaskStatusesContainer() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(LHTaskStatusesContainer.class)
                .build();
    }

    @BuildStep
    AdditionalBeanBuildItem produceLittleHorseReactiveStub() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(LittleHorseReactiveStub.class)
                .build();
    }
}

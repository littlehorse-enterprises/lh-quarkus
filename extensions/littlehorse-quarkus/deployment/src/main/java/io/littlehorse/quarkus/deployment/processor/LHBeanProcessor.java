package io.littlehorse.quarkus.deployment.processor;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.reactive.LittleHorseReactiveStub;
import io.littlehorse.quarkus.runtime.LHExternalBeans;
import io.littlehorse.quarkus.runtime.LHTaskStatusesContainer;
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
    AdditionalBeanBuildItem produceLHTaskStatusesContainer() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(LHTaskStatusesContainer.class)
                .build();
    }

    @BuildStep
    AdditionalBeanBuildItem produceConfigEvaluator() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(ConfigEvaluator.class)
                .build();
    }

    @BuildStep
    AdditionalBeanBuildItem produceLittleHorseReactiveStub() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(LittleHorseReactiveStub.class)
                .build();
    }
}

package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.recordable.LHWorkflowConsumerRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowConsumerBuildItem extends MultiBuildItem {
    private final String beanClassName;
    private final String wfSpecName;

    public LHWorkflowConsumerBuildItem(String beanClassName, String wfSpecName) {
        this.wfSpecName = wfSpecName;
        this.beanClassName = beanClassName;
    }

    public String getWfSpecName() {
        return wfSpecName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public LHWorkflowConsumerRecordable toRecordable() {
        return new LHWorkflowConsumerRecordable(beanClassName, wfSpecName);
    }
}

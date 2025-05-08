package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.recordable.LHWorkflowFromMethodRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowFromMethodBuildItem extends MultiBuildItem {
    private final String beanClassName;
    private final String beanMethodName;
    private final String wfSpecName;

    public LHWorkflowFromMethodBuildItem(
            String beanClassName, String beanMethodName, String wfSpecName) {
        this.wfSpecName = wfSpecName;
        this.beanClassName = beanClassName;
        this.beanMethodName = beanMethodName;
    }

    public String getWfSpecName() {
        return wfSpecName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public String getBeanMethodName() {
        return beanMethodName;
    }

    public LHWorkflowFromMethodRecordable toRecordable() {
        return new LHWorkflowFromMethodRecordable(beanClassName, beanMethodName, wfSpecName);
    }
}

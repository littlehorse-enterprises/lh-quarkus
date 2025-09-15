package io.littlehorse.quarkus.deployment.collection;

import static org.assertj.core.api.Assertions.assertThat;

import io.littlehorse.quarkus.runtime.recordable.LHWorkflowRecordable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

import org.junit.jupiter.api.Test;

import java.util.List;

class LHWorkflowRecordableGraphTest {

    private static LHWorkflowRecordable newRecordable(String name, String parent) {
        return new LHWorkflowRecordable(null, name, parent, null, null, null, null, null, null) {
            @Override
            public void buildWorkflowThread(WorkflowThread workflowThread) {}
        };
    }

    @Test
    void shouldSortChildAndParent() {
        LHWorkflowRecordable child = newRecordable("my-child", "my-parent");
        LHWorkflowRecordable parent = newRecordable("my-parent", null);

        assertThat(LHWorkflowRecordableGraph.sort(List.of(child, parent)))
                .containsExactly(parent, child);
    }

    @Test
    void shouldSortMultipleParent() {
        LHWorkflowRecordable child = newRecordable("my-child", "my-parent1");
        LHWorkflowRecordable parent1 = newRecordable("my-parent1", "my-parent2");
        LHWorkflowRecordable parent2 = newRecordable("my-parent2", null);

        assertThat(LHWorkflowRecordableGraph.sort(List.of(child, parent1, parent2)))
                .containsExactly(parent2, parent1, child);
    }

    @Test
    void shouldSortNotParent() {
        LHWorkflowRecordable other = newRecordable("other", null);
        LHWorkflowRecordable child = newRecordable("my-child", "my-parent1");
        LHWorkflowRecordable parent1 = newRecordable("my-parent1", "my-parent2");
        LHWorkflowRecordable parent2 = newRecordable("my-parent2", null);

        assertThat(LHWorkflowRecordableGraph.sort(List.of(child, parent1, parent2, other)))
                .containsExactly(parent2, other, parent1, child);
    }

    @Test
    void shouldSortWithMultipleChild() {
        LHWorkflowRecordable wf1 = newRecordable("wf1", "wf2");
        LHWorkflowRecordable wf2 = newRecordable("wf2", "wf3");
        LHWorkflowRecordable wf3 = newRecordable("wf3", null);
        LHWorkflowRecordable wf4 = newRecordable("wf4", null);
        LHWorkflowRecordable wf5 = newRecordable("wf5", "wf3");

        assertThat(LHWorkflowRecordableGraph.sort(List.of(wf4, wf5, wf3, wf1, wf2)))
                .containsExactly(wf4, wf3, wf5, wf2, wf1);
    }

    @Test
    void shouldSortMissingParent() {
        LHWorkflowRecordable wf1 = newRecordable("wf1", "wf2");
        LHWorkflowRecordable wf2 = newRecordable("wf2", "wf3");
        LHWorkflowRecordable wf4 = newRecordable("wf4", null);
        LHWorkflowRecordable wf5 = newRecordable("wf5", "wf3");

        assertThat(LHWorkflowRecordableGraph.sort(List.of(wf4, wf5, wf1, wf2)))
                .containsExactly(wf4, wf5, wf2, wf1);
    }
}

package io.littlehorse.quarkus.deployment.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.littlehorse.quarkus.deployment.item.LHUserTaskDefBuildItem;
import io.littlehorse.quarkus.task.LHUserTaskDef;
import io.littlehorse.sdk.worker.LHStructDef;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;

import org.jboss.jandex.Index;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class LHUserTaskDefProcessorTest {
    @Test
    void shouldDiscoverDefinitionsSharingANamedResultStruct() throws IOException {
        var items = scan(Approve.class, Review.class, Result.class);
        assertThat(items).hasSize(2);
        assertThat(items)
                .map(item -> item.toRecordable().getName())
                .containsExactlyInAnyOrder("approve", "review");
        assertThat(items)
                .allSatisfy(item -> assertThat(item.toRecordable().getResultStructDefName())
                        .isEqualTo("${approval.schema:approval-result}"));
    }

    @Test
    void shouldRejectAnInlineOrUnannotatedResultClass() {
        assertThatThrownBy(() -> scan(Invalid.class, UnnamedResult.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid", "must declare a named @LHStructDef");
    }

    @Test
    void shouldIncludeNestedUserTaskResultTypesInNativeReflection() throws IOException {
        Index index = Index.of(Approve.class, Result.class, NestedResult.class);
        assertThat(LHReflectionProcessor.reflectiveTypeNames(index))
                .contains(Result.class.getName(), NestedResult.class.getName());
    }

    private static List<LHUserTaskDefBuildItem> scan(Class<?>... classes) throws IOException {
        Index index = Index.of(classes);
        var items = new ArrayList<LHUserTaskDefBuildItem>();
        new LHServiceProcessor()
                .scanLHUserTaskDef(
                        items::add, new BeanArchiveIndexBuildItem(index, index, Set.of()));
        return items;
    }

    @LHUserTaskDef(value = "approve", result = Result.class)
    public static class Approve {}

    @LHUserTaskDef(value = "review", result = Result.class)
    public static class Review {}

    @LHUserTaskDef(value = "invalid", result = UnnamedResult.class)
    public static class Invalid {}

    public static class UnnamedResult {}

    @LHStructDef("${approval.schema:approval-result}")
    public static class Result {
        public NestedResult getDetails() {
            return null;
        }
    }

    public static class NestedResult {
        public String getName() {
            return null;
        }
    }
}

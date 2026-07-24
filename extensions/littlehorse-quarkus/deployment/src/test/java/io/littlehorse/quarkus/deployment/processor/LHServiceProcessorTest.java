package io.littlehorse.quarkus.deployment.processor;

import static org.assertj.core.api.Assertions.assertThat;

import io.littlehorse.quarkus.deployment.item.LHTaskMethodBuildItem;
import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.littlehorse.sdk.common.proto.InlineStruct;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;

import org.jboss.jandex.Index;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

class LHServiceProcessorTest {

    @Test
    void shouldCollectInputAndOutputStructDefNameTemplates() throws IOException {
        assertThat(scanTask("transform").getStructDefNameTemplates())
                .containsExactlyInAnyOrder("${raw.input.struct.name}", "${raw.output.struct.name}");
    }

    @Test
    void shouldDeduplicateStructDefNameTemplatesDeterministically() throws IOException {
        assertThat(scanTask("reuse").getStructDefNameTemplates())
                .containsExactlyInAnyOrder("${shared.struct.name}");
    }

    private static LHTaskMethodRecordable scanTask(String taskName) throws IOException {
        Index index = Index.of(RawInlineStructTask.class);
        BeanArchiveIndexBuildItem indexBuildItem =
                new BeanArchiveIndexBuildItem(index, index, Set.of());
        ArrayList<LHTaskMethodBuildItem> taskMethods = new ArrayList<>();

        new LHServiceProcessor().scanLHTaskMethod(taskMethods::add, indexBuildItem);

        return taskMethods.stream()
                .map(LHTaskMethodBuildItem::toRecordable)
                .filter(recordable -> recordable.getName().equals(taskName))
                .findFirst()
                .orElseThrow();
    }

    static class RawInlineStructTask {

        @LHTaskMethod("transform")
        @LHType(structDefName = "${raw.output.struct.name}")
        InlineStruct transform(
                @LHType(structDefName = "${raw.input.struct.name}") InlineStruct input) {
            return input;
        }

        @LHTaskMethod("reuse")
        @LHType(structDefName = "${shared.struct.name}")
        InlineStruct reuse(@LHType(structDefName = "${shared.struct.name}") InlineStruct input) {
            return input;
        }
    }
}

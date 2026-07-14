package io.littlehorse.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.SaddleBagManifest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class SaddleBagTaskMetadataTest {

    @Test
    void shouldEmitTaskWithStructOutputAndPrimitiveInputs() {
        JsonNode task = SaddleBagManifest.task(SaddleBagManifest.read(), "create-address");

        assertThat(task.isMissingNode()).isFalse();
        assertThat(task.path("description").asText()).isEqualTo("Builds an address from its parts");
        assertThat(task.path("output").path("type").path("struct").asText()).isEqualTo("address");

        JsonNode inputs = task.path("inputs");
        assertThat(inputs).hasSize(2);
        assertThat(inputs.get(0).path("name").asText()).isEqualTo("street");
        assertThat(inputs.get(0).path("type").path("primitive").asText()).isEqualTo("STR");
        assertThat(inputs.get(1).path("name").asText()).isEqualTo("number");
        assertThat(inputs.get(1).path("type").path("primitive").asText()).isEqualTo("INT");
    }

    @Test
    void shouldEmitTaskWithPrimitiveOutput() {
        JsonNode task = SaddleBagManifest.task(SaddleBagManifest.read(), "send-email");

        assertThat(task.isMissingNode()).isFalse();
        assertThat(task.path("output").path("type").path("primitive").asText()).isEqualTo("BOOL");
    }
}

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
class SaddleBagStructMetadataTest {

    @Test
    void shouldEmitStructWithPropertyTypes() {
        JsonNode struct = SaddleBagManifest.struct(SaddleBagManifest.read(), "address");
        assertThat(struct.isMissingNode()).isFalse();

        JsonNode properties = struct.path("properties");

        JsonNode street = SaddleBagManifest.findByField(properties, "name", "street");
        assertThat(street.isMissingNode()).isFalse();
        assertThat(street.path("type").path("primitive").asText()).isEqualTo("STR");

        JsonNode number = SaddleBagManifest.findByField(properties, "name", "number");
        assertThat(number.isMissingNode()).isFalse();
        assertThat(number.path("type").path("primitive").asText()).isEqualTo("INT");
    }
}

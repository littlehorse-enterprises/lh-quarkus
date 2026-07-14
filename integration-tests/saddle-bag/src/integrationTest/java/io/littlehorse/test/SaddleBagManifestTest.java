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
class SaddleBagManifestTest {

    @Test
    void shouldGenerateManifestWithMetadata() {
        JsonNode manifest = SaddleBagManifest.read();

        assertThat(manifest.path("name").asText()).isEqualTo("integration-tests-saddle-bag");
        assertThat(manifest.path("title").asText()).isEqualTo("Integration Tests Saddle Bag");
        assertThat(manifest.path("author").asText()).isEqualTo("LittleHorse");
        assertThat(manifest.path("description").asText())
                .isEqualTo("Saddle bag manifest exercised by the integration tests");
        assertThat(manifest.path("version").asText()).isNotBlank();
    }

    @Test
    void shouldGenerateManifestWithMetadataBlock() {
        JsonNode metadata = SaddleBagManifest.read().path("metadata");

        assertThat(metadata.path("licence").asText()).isEqualTo("Apache-2.0");
        assertThat(SaddleBagManifest.texts(metadata.path("tags"))).contains("test", "integration");
    }
}

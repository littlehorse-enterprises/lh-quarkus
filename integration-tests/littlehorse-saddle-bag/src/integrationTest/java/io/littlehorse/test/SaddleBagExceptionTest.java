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
class SaddleBagExceptionTest {

    @Test
    void shouldEmitAnnotatedBusinessException() {
        JsonNode task = SaddleBagManifest.task(SaddleBagManifest.read(), "send-email");
        JsonNode exception =
                SaddleBagManifest.findByField(task.path("exceptions"), "name", "invalid-address");

        assertThat(exception.isMissingNode()).isFalse();
        assertThat(exception.path("description").asText())
                .isEqualTo("The recipient email address is invalid");
    }

    @Test
    void shouldOmitExceptionsForTasksWithoutAny() {
        JsonNode task = SaddleBagManifest.task(SaddleBagManifest.read(), "create-address");

        assertThat(task.has("exceptions")).isFalse();
    }
}

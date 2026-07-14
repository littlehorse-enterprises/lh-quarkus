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
class SaddleBagConfigTest {

    @Test
    void shouldEmitClassLevelConfigsAsGlobalConfigs() {
        JsonNode manifest = SaddleBagManifest.read();
        JsonNode configs = manifest.path("configs");

        JsonNode host = SaddleBagManifest.findByField(configs, "key", "smtp.host");
        assertThat(host.isMissingNode()).isFalse();
        assertThat(host.path("description").asText()).isEqualTo("SMTP server hostname");
        assertThat(host.path("sensitive").asBoolean()).isFalse();
        assertThat(host.path("type").path("primitive").asText()).isEqualTo("STR");

        JsonNode password = SaddleBagManifest.findByField(configs, "key", "smtp.password");
        assertThat(password.isMissingNode()).isFalse();
        assertThat(password.path("sensitive").asBoolean()).isTrue();
        assertThat(password.path("type").path("primitive").asText()).isEqualTo("STR");
    }

    @Test
    void shouldEmitMethodLevelConfigInsideOwningTask() {
        JsonNode task = SaddleBagManifest.task(SaddleBagManifest.read(), "send-email");
        JsonNode port = SaddleBagManifest.findByField(task.path("configs"), "key", "smtp.port");

        assertThat(port.isMissingNode()).isFalse();
        assertThat(port.path("description").asText()).isEqualTo("SMTP server port");
        assertThat(port.path("default-value").asText()).isEqualTo("587");
        assertThat(port.path("type").path("primitive").asText()).isEqualTo("INT");
    }
}

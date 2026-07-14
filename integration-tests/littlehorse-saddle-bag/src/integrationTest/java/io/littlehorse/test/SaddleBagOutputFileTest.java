package io.littlehorse.test;

import static org.assertj.core.api.Assertions.assertThat;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.SaddleBagManifest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class SaddleBagOutputFileTest {

    @Test
    void shouldGenerateOutputFile() throws Exception {
        assertThat(Files.exists(SaddleBagManifest.MANIFEST_FILE)).isTrue();
        assertThat(Files.size(SaddleBagManifest.MANIFEST_FILE)).isPositive();
    }
}

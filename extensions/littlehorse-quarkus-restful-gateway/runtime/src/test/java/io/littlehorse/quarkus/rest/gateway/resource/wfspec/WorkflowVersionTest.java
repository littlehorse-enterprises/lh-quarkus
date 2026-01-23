package io.littlehorse.quarkus.rest.gateway.resource.wfspec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.ws.rs.BadRequestException;

import org.junit.jupiter.api.Test;

class WorkflowVersionTest {

    private static final String ERROR_MESSAGE = "Version should be in the format major.revision";

    @Test
    void shouldThrowExceptionIfVersionIsInvalid() {
        BadRequestException result = assertThrows(BadRequestException.class, () -> {
            WorkflowVersion.of("invalidVersion");
        });

        assertThat(result.getMessage()).isEqualTo(ERROR_MESSAGE);
    }

    @Test
    void shouldThrowExceptionIfVersionIsEmpty() {
        BadRequestException result = assertThrows(BadRequestException.class, () -> {
            WorkflowVersion.of("");
        });

        assertThat(result.getMessage()).isEqualTo(ERROR_MESSAGE);
    }

    @Test
    void shouldThrowExceptionIfVersionIsNull() {
        BadRequestException result = assertThrows(BadRequestException.class, () -> {
            WorkflowVersion.of(null);
        });

        assertThat(result.getMessage()).isEqualTo(ERROR_MESSAGE);
    }
}

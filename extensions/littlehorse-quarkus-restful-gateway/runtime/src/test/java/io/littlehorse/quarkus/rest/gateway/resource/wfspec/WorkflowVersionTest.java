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
            new WorkflowVersion("invalidVersion");
        });

        assertThat(result.getMessage()).isEqualTo(ERROR_MESSAGE);
    }

    @Test
    void shouldThrowExceptionIfVersionIsEmpty() {
        BadRequestException result = assertThrows(BadRequestException.class, () -> {
            new WorkflowVersion("");
        });

        assertThat(result.getMessage()).isEqualTo(ERROR_MESSAGE);
    }

    @Test
    void shouldThrowExceptionIfVersionIsNull() {
        BadRequestException result = assertThrows(BadRequestException.class, () -> {
            new WorkflowVersion(null);
        });

        assertThat(result.getMessage()).isEqualTo(ERROR_MESSAGE);
    }

    @Test
    void shouldParseVersionToInt() {
        WorkflowVersion workflowVersion = new WorkflowVersion("1.4");

        assertThat(workflowVersion.majorVersion()).isEqualTo(1);
        assertThat(workflowVersion.revision()).isEqualTo(4);
    }

    @Test
    void shouldToString() {
        String expected = "5.3";
        WorkflowVersion workflowVersion = new WorkflowVersion(expected);

        assertThat(workflowVersion.toString()).isEqualTo(expected);
    }
}

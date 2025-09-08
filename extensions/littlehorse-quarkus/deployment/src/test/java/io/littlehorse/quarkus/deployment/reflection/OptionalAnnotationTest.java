package io.littlehorse.quarkus.deployment.reflection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class OptionalAnnotationTest {

    @Test
    void shouldReturnNullIfReceivesNull() {
        OptionalAnnotation annotationHelper = new OptionalAnnotation(null);

        String myValue = assertDoesNotThrow(() -> annotationHelper.getValue("myValue"));

        assertThat(myValue).isNull();
    }
}

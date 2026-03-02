package io.littlehorse.quarkus.deployment.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.jboss.jandex.AnnotationInstance;
import org.junit.jupiter.api.Test;

class OptionalAnnotationTest {

    @Test
    void shouldReturnNullIfReceivesNull() {
        OptionalAnnotation annotationHelper = new OptionalAnnotation((AnnotationInstance) null);

        String myValue = assertDoesNotThrow(() -> annotationHelper.getValue("myValue"));

        assertThat(myValue).isNull();
    }
}

package io.littlehorse.quarkus.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.NoSuchElementException;

class ConfigExpressionTest {
    @Test
    void shouldReadConfig() {
        String expression = "my-value";
        ConfigExpression expanded = ConfigExpression.expand(expression);

        assertThat(expanded.isExpression()).isFalse();
        assertThat(expanded.asString()).isEqualTo("my-value");
        assertThat(expanded.getMembers()).isEqualTo(Map.of());
    }

    @Test
    void shouldFailIfNotFoundConfig() {
        String expression = "${not.a.config}";

        assertThrows(NoSuchElementException.class, () -> ConfigExpression.expand(expression));
    }

    @Test
    void shouldExpandConfig() {
        String expression = "${my.config.test}";
        ConfigExpression expanded = ConfigExpression.expand(expression);

        assertThat(expanded.isExpression()).isTrue();
        assertThat(expanded.asString()).isEqualTo("this-is-a-test");
        assertThat(expanded.getMembers()).isEqualTo(Map.of("my.config.test", "this-is-a-test"));
    }

    @Test
    void shouldExpandDefaultConfig() {
        String expression = "${not.a.config:default-value}";
        ConfigExpression expanded = ConfigExpression.expand(expression);

        assertThat(expanded.isExpression()).isTrue();
        assertThat(expanded.asString()).isEqualTo("default-value");
        assertThat(expanded.getMembers()).isEqualTo(Map.of("not.a.config", "default-value"));
    }

    @Test
    void shouldExpandConcatenatedConfig() {
        String expression = "concatenation-${my.config.test}";
        ConfigExpression expanded = ConfigExpression.expand(expression);

        assertThat(expanded.isExpression()).isTrue();
        assertThat(expanded.asString()).isEqualTo("concatenation-this-is-a-test");
        assertThat(expanded.getMembers()).isEqualTo(Map.of("my.config.test", "this-is-a-test"));
    }

    @Test
    void shouldExpandMultipleConfig() {
        String expression = "${my.config.test}-${my.second.config.test}";
        ConfigExpression expanded = ConfigExpression.expand(expression);

        assertThat(expanded.isExpression()).isTrue();
        assertThat(expanded.asString()).isEqualTo("this-is-a-test-this-is-a-second-test");
        assertThat(expanded.getMembers())
                .isEqualTo(Map.of(
                        "my.config.test",
                        "this-is-a-test",
                        "my.second.config.test",
                        "this-is-a-second-test"));
    }

    @Test
    void shouldComposeConfig() {
        String expression = "compose-${${my.compose.config}}";
        ConfigExpression expanded = ConfigExpression.expand(expression);

        assertThat(expanded.isExpression()).isTrue();
        assertThat(expanded.asString()).isEqualTo("compose-this-is-a-test");
        assertThat(expanded.getMembers())
                .isEqualTo(Map.of(
                        "my.compose.config", "my.config.test", "my.config.test", "this-is-a-test"));
    }
}

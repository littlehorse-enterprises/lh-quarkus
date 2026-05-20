package io.littlehorse.quarkus.saddle.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a configuration property that is required by an {@code @LHTask} class at runtime.
 *
 * <p>Use this annotation on classes annotated with {@code @LHTask} to document external
 * configuration dependencies (e.g., API URLs, credentials, service endpoints) that consumers
 * of the saddle-bag must provide in their {@code application.properties}.
 *
 * <p>Example:
 * <pre>{@code
 * @LHTask
 * @LHRequiredConfig(value = "smtp.host", description = "SMTP server hostname")
 * @LHRequiredConfig(value = "smtp.password", description = "SMTP password", secret = true)
 * public class EmailTask {
 *     // ...
 * }
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LHRequiredConfigs.class)
public @interface LHRequiredConfig {

    /**
     * The configuration property key (e.g., {@code "smtp.host"}, {@code "api.service.url"}).
     */
    String value();

    /**
     * Human-readable description of what this configuration property is used for.
     */
    String description() default "";

    /**
     * Whether this configuration property contains sensitive data (e.g., passwords, API keys).
     * Secret values should not be logged or exposed in plain text.
     */
    boolean secret() default false;

    /**
     * Default value for this configuration property. Empty string means no default (mandatory).
     */
    String defaultValue() default "";
}

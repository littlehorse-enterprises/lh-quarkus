package io.littlehorse.quarkus.saddle.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a configuration property that is required at runtime.
 *
 * <p>Use this annotation to document external configuration dependencies (e.g., API URLs,
 * credentials, service endpoints) that consumers of the saddle-bag must provide in their
 * {@code application.properties}. It can be placed on:
 *
 * <ul>
 *   <li>a class annotated with {@code @LHTask} — emitted as a <em>global</em> saddle-bag config
 *       under the top-level {@code configs} field;
 *   <li>a method annotated with {@code @LHTaskMethod} — emitted under the {@code configs} field of
 *       that specific task.
 * </ul>
 *
 * <p>Example:
 * <pre>{@code
 * @LHTask
 * @LHTaskConfig(value = "smtp.host", description = "SMTP server hostname", type = LHTaskConfigType.STR)
 * @LHTaskConfig(value = "smtp.password", description = "SMTP password", sensitive = true, type = LHTaskConfigType.STR)
 * public class EmailTask {
 *
 *     @LHTaskMethod("${task.send-email.name}")
 *     @LHTaskConfig(value = "smtp.port", description = "SMTP port", defaultValue = "587", type = LHTaskConfigType.INT)
 *     public void sendEmail(String recipient) {
 *         // ...
 *     }
 * }
 * }</pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LHTaskConfigs.class)
public @interface LHTaskConfig {

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
     * Sensitive values should not be logged or exposed in plain text.
     */
    boolean sensitive() default false;

    /**
     * Default value for this configuration property. Empty string means no default (mandatory).
     */
    String defaultValue() default "";

    /**
     * Variable type of the config property. Used for validation.
     */
    LHTaskConfigType type();

    /**
     * Variable type of the config property. Required for validation.
     */
    enum LHTaskConfigType {
        STR,
        INT,
        DOUBLE,
        BOOL
    }
}

package io.littlehorse.quarkus.saddle.config;

import io.littlehorse.quarkus.saddle.config.LHTaskConfig.LHTaskConfigType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a configuration property that is required by a single {@code @LHTaskMethod} at runtime.
 *
 * <p>Use this annotation on methods annotated with {@code @LHTaskMethod} to document external
 * configuration dependencies that are specific to that task method. Unlike {@link LHTaskConfig},
 * which declares configs at the class level and is emitted as a <em>global</em> saddle-bag config,
 * an {@code @LHTaskMethodConfig} is emitted inside the specific task in the generated manifest.
 *
 * <p>A given configuration key may only be declared by a single task method. If two different task
 * methods declare the same key, the build fails: shared configuration should instead be declared
 * once at the class level with {@link LHTaskConfig} so it becomes a global saddle-bag config.
 *
 * <p>Example:
 * <pre>{@code
 * @LHTaskMethod("charge-credit-card")
 * @LHTaskMethodConfig(value = "payment.gateway.url", description = "Payment gateway URL", type = LHTaskConfigType.STR)
 * public void chargeCreditCard(String userId, double amount) {
 *     // ...
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LHTaskMethodConfigs.class)
public @interface LHTaskMethodConfig {

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
}

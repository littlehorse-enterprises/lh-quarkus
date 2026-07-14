package io.littlehorse.quarkus.saddle.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a business {@code EXCEPTION} that an {@code @LHTaskMethod} may throw at runtime.
 *
 * <p>Use this annotation on methods annotated with {@code @LHTaskMethod} to document the
 * business exceptions (thrown via {@code io.littlehorse.sdk.common.exception.LHTaskException}
 * or a subclass) that a {@code WfSpec} may catch. Business exception names use kebab-case
 * (e.g. {@code "insufficient-funds"}).
 *
 * <p>Example:
 * <pre>{@code
 * @LHTaskMethod("charge-credit-card")
 * @LHTaskMethodException(name = "insufficient-funds", description = "Card balance too low")
 * @LHTaskMethodException(name = "amount-too-large", description = "Charge exceeds $10,000")
 * public void chargeCreditCard(String userId, double amount) {
 *     // ...
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LHTaskMethodExceptions.class)
public @interface LHTaskMethodException {

    /**
     * The business exception name thrown by the task (kebab-case, e.g. {@code "insufficient-funds"}).
     */
    String name();

    /**
     * Human-readable description of when and why this exception is thrown.
     */
    String description() default "";
}

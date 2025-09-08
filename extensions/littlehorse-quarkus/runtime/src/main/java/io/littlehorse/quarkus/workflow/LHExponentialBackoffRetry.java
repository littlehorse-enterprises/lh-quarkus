package io.littlehorse.quarkus.workflow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LHExponentialBackoffRetry {

    /**
     * Base delay in ms for the first retry (as int).
     * Note that in LittleHorse, timers have a resolution of 500-1000 milliseconds.
     * Must be greater than zero.
     */
    String baseIntervalMs() default "";

    /**
     * The multiplier to use in calculating the retry backoff policy (as float).
     * We recommend starting with 2.0. Must be at least 1.0.
     */
    String multiplier() default "";

    /**
     * Maximum delay in milliseconds between retries (as int).
     */
    String maxDelayMs() default "";
}

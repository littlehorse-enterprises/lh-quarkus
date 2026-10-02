package io.littlehorse.quarkus.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Registers a user task definition whose result is a named StructDef. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LHUserTaskDef {
    /** The name of the UserTaskDef. Configuration placeholders are supported. */
    String value();

    /** The result class, which must be annotated with {@code @LHStructDef}. */
    Class<?> result();
}

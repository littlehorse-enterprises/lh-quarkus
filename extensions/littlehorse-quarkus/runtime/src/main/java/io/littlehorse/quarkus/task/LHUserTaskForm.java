package io.littlehorse.quarkus.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LHUserTaskForm {
    /**
     * The name of the user task definition (UserTaskDef).
     */
    String value();
}

package io.littlehorse.quarkus.workflow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers a type and its nested JavaBean property types for reflection.
 *
 * <p>Use this annotation for workflow-only types that cannot be discovered by compiling the
 * workflow at build time, such as definitions requiring runtime CDI injection or types selected
 * by runtime configuration. It does not register a CDI bean or a named LittleHorse StructDef.
 *
 * <p>Workflows that can compile during native-image augmentation have their referenced types
 * registered automatically using the LittleHorse SDK.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LHReflectiveType {}

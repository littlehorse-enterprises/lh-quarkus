package io.littlehorse.quarkus.workflow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers a type and its nested JavaBean property types for reflection.
 *
 * <p>Use this annotation for application types referenced only by a workflow specification, such
 * as a type passed to {@code WorkflowThread.declareInlineStruct}. It does not register a CDI bean
 * or a named LittleHorse StructDef.
 *
 * <p>This annotation is a compatibility mechanism until the LittleHorse SDK exposes the types
 * referenced by a workflow specification.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LHReflectiveType {}

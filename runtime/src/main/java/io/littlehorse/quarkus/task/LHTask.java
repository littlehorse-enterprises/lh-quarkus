package io.littlehorse.quarkus.task;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@RegisterForReflection
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LHTask {}

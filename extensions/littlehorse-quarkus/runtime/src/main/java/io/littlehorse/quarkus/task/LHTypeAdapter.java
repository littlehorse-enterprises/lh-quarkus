package io.littlehorse.quarkus.task;

import io.littlehorse.sdk.common.proto.VariableType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LHTypeAdapter {

    VariableType variableType();

    Class<?> adaptedType();
}

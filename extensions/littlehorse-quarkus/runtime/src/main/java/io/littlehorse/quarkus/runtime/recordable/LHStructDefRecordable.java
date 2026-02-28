package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.runtime.register.LHStructDefRegister;
import io.littlehorse.sdk.common.proto.PutStructDefRequest;
import io.littlehorse.sdk.common.proto.StructDef;
import io.littlehorse.sdk.common.proto.StructDefId;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructDefType;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

import java.util.List;

public class LHStructDefRecordable extends LHRecordable {

    private final String description;

    @RecordableConstructor
    public LHStructDefRecordable(Class<?> beanClass, String name, String description) {
        super(beanClass, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public List<String> dependencies() {
        LHStructDefType structDefType = new LHStructDefType(getBeanClass());
        return structDefType.getDependencyClasses().stream()
                .map(LHStructDefType::getStructDefId)
                .map(StructDefId::getName)
                .filter(name -> !name.equals(getName()))
                .toList();
    }

    public void registerStructDef() {
        if (!exists()) return;

        ConfigEvaluator configEvaluator = new ConfigEvaluator();
        LHStructDefType structDefType = new LHStructDefType(getBeanClass());
        StructDef structDef = structDefType.toStructDef();
        PutStructDefRequest request = PutStructDefRequest.newBuilder()
                .setStructDef(structDef.getStructDef())
                .setName(configEvaluator.expand(structDef.getId().getName()).asString())
                .setDescription(
                        configEvaluator.expand(structDef.getDescription()).asString())
                .build();

        LHStructDefRegister register =
                CDI.current().select(LHStructDefRegister.class).get();
        register.registerStructDef(request);
    }
}

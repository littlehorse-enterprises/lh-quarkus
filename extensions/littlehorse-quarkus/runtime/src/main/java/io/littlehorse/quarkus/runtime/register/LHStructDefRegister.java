package io.littlehorse.quarkus.runtime.register;

import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutStructDefRequest;
import io.littlehorse.sdk.worker.LHStructDef;
import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Unremovable
public class LHStructDefRegister {

    private static final Logger log = LoggerFactory.getLogger(LHStructDefRegister.class);
    private final LittleHorseBlockingStub blockingStub;
    private final LHRuntimeConfig config;

    public LHStructDefRegister(LittleHorseBlockingStub blockingStub, LHRuntimeConfig config) {
        this.blockingStub = blockingStub;
        this.config = config;
    }

    public void registerStructDef(PutStructDefRequest request) {
        if (!config.structsRegisterEnabled()) return;

        log.info("Registering {}: {}", LHStructDef.class.getSimpleName(), request.getName());
        blockingStub.putStructDef(request);
    }
}

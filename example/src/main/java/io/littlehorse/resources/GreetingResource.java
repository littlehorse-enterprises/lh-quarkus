package io.littlehorse.resources;

import static io.littlehorse.configs.LittleHorseBeans.TASK_GREETINGS;
import static io.littlehorse.configs.LittleHorseBeans.VAR_NAME;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.RunWfRequest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/hello")
public class GreetingResource {

    final LHConfig config;
    final LittleHorseBlockingStub blockingStub;

    public GreetingResource(LHConfig config) {
        this.config = config;
        this.blockingStub = config.getBlockingStub();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@QueryParam("name") String name) {
        String message = "Hello there! " + name;
        log.info("Executing rest /hello: {}", message);
        RunWfRequest request = RunWfRequest.newBuilder()
                .setWfSpecName(TASK_GREETINGS)
                .putVariables(VAR_NAME, LHLibUtil.objToVarVal(name))
                .build();
        blockingStub.runWf(request);
        return message;
    }
}

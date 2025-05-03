package io.littlehorse.configs;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.quarkus.runtime.Startup;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LittleHorseStartup {

    private static final Logger log = LoggerFactory.getLogger(LittleHorseStartup.class);

    final Workflow workflow;
    final LHConfig config;

    public LittleHorseStartup(LHConfig config, Workflow workflow) {
        this.workflow = workflow;
        this.config = config;
    }

    @Startup
    void startup() {
        log.info("Registering workflow");
        workflow.registerWfSpec(config.getBlockingStub());
    }
}

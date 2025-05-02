package io.littlehorse.configs;

import io.littlehorse.workers.GreetingsTask;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LittleHorseStartup {

    private final GreetingsTask task;

    public LittleHorseStartup(GreetingsTask task) {
        this.task = task;
    }

    //    private static final Logger log = LoggerFactory.getLogger(LittleHorseStartup.class);
    //
    //    final Workflow workflow;
    //    final LHConfig config;
    //    final LHTaskWorker taskGreetings;
    //    final LHTaskWorker taskPrint;
    //
    //    public LittleHorseStartup(
    //            LHConfig config,
    //            Workflow workflow,
    //            @Identifier(TASK_GREETINGS) LHTaskWorker taskGreetings,
    //            @Identifier(TASK_PRINT) LHTaskWorker taskPrint) {
    //        this.workflow = workflow;
    //        this.taskGreetings = taskGreetings;
    //        this.config = config;
    //        this.taskPrint = taskPrint;
    //    }
    //
    //    @Startup
    //    void startup() {
    //        log.info("Registering tasks");
    //        taskGreetings.registerTaskDef();
    //        taskPrint.registerTaskDef();
    //
    //        log.info("Starting workers");
    //        taskGreetings.start();
    //        taskPrint.start();
    //
    //        log.info("Registering workflow");
    //        workflow.registerWfSpec(config.getBlockingStub());
    //    }
    //
    //    @Shutdown
    //    void shutdown() {
    //        log.info("Stopping workers");
    //        taskGreetings.close();
    //        taskPrint.close();
    //    }
}

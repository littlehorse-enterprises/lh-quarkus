package io.littlehorse.proxy.dev;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class PrintTask {

    @LHTaskMethod("${task.print.name}")
    public void print(String message) {
        System.out.println(message);
    }
}

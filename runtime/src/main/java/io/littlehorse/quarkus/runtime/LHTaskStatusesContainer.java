package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.runtime.health.LHTaskStatus;
import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Unremovable
public class LHTaskStatusesContainer {

    private final List<LHTaskStatus> taskStatuses = Collections.synchronizedList(new ArrayList<>());

    public List<LHTaskStatus> getTaskStatuses() {
        return taskStatuses;
    }

    public void add(LHTaskStatus taskStatus) {
        taskStatuses.add(taskStatus);
    }
}

package io.littlehorse.saddlebag.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

import java.util.UUID;

@LHTask
public class TrackingTask {

    public static final String GENERATE_TRACKING_ID = "generate-tracking-id";
    public static final String LOOKUP_TRACKING = "lookup-tracking";

    @LHTaskMethod(GENERATE_TRACKING_ID)
    public UUID generateTrackingId() {
        return UUID.randomUUID();
    }

    @LHTaskMethod(LOOKUP_TRACKING)
    public String lookupTracking(UUID trackingId) {
        return "Status for %s: IN_TRANSIT".formatted(trackingId);
    }
}

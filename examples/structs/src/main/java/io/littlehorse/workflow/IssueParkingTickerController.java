package io.littlehorse.workflow;

import static io.littlehorse.workflow.IssueParkingTicketWorkflow.CAR_INPUT_VAR;
import static io.littlehorse.workflow.IssueParkingTicketWorkflow.ISSUE_PARKING_TICKET_WF;

import io.littlehorse.quarkus.reactive.LittleHorseReactiveStub;
import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.WfRun;
import io.littlehorse.structs.ParkingTicketReport;
import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/tickets")
public class IssueParkingTickerController {

    private final LittleHorseReactiveStub littleHorseReactiveStub;

    public IssueParkingTickerController(LittleHorseReactiveStub littleHorseReactiveStub) {
        this.littleHorseReactiveStub = littleHorseReactiveStub;
    }

    @POST
    public Uni<WfRun> issue(ParkingTicketReport report) {
        return littleHorseReactiveStub.runWf(RunWfRequest.newBuilder()
                .setWfSpecName(ISSUE_PARKING_TICKET_WF)
                .putVariables(CAR_INPUT_VAR, LHLibUtil.objToVarVal(report))
                .build());
    }
}

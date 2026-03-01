package io.littlehorse.workflow;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.structs.Address;
import io.littlehorse.structs.ParkingTicketReport;
import io.littlehorse.structs.Person;

@LHTask
public class IssueParkingTicketWorkflow {

    public static final String ISSUE_PARKING_TICKET_WF = "issue-parking-ticket";
    public static final String CAR_INPUT_VAR = "car-input";
    public static final String CAR_OWNER_VAR = "car-owner";
    public static final String GET_CAR_OWNER_TASK = "get-car-owner";
    public static final String MAIL_TICKET_TASK = "mail-ticket";

    @LHWorkflow(ISSUE_PARKING_TICKET_WF)
    public void workflow(WorkflowThread wf) {
        WfRunVariable carInput =
                wf.declareStruct(CAR_INPUT_VAR, ParkingTicketReport.class).required();
        WfRunVariable carOwner = wf.declareStruct(CAR_OWNER_VAR, Person.class);

        carOwner.assign(wf.execute(GET_CAR_OWNER_TASK, carInput));
        wf.execute(MAIL_TICKET_TASK, carInput, carOwner);
    }

    @LHTaskMethod(GET_CAR_OWNER_TASK)
    public Person getCarOwner(ParkingTicketReport report) {
        // Simulates a database lookup...
        return new Person(
                "Obi-Wan",
                "Kenobi",
                new Address(124, "Sand Dune Lane", "Anchorhead", "Tattooine", 97412));
    }

    @LHTaskMethod(MAIL_TICKET_TASK)
    public String mailTicket(ParkingTicketReport report, Person person) {
        System.out.printf("Notifying %s of parking ticket: %s.%n", person, report);
        return "Ticket sent to %s at %s".formatted(person, person.getHomeAddress());
    }
}

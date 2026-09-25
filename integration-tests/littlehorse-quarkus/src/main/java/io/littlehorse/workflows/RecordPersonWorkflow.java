package io.littlehorse.workflows;

import static io.littlehorse.tasks.RecordPersonTask.BUILD_RECORD_PERSON_TASK;
import static io.littlehorse.tasks.RecordPersonTask.DESCRIBE_RECORD_PERSON_TASK;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.structs.RecordPerson;

@LHWorkflow(RecordPersonWorkflow.RECORD_PERSON_WORKFLOW)
public class RecordPersonWorkflow implements LHWorkflowDefinition {

    public static final String RECORD_PERSON_WORKFLOW = "record-person-wf";
    public static final String FIRST_NAME_VARIABLE = "record-first-name";
    public static final String LAST_NAME_VARIABLE = "record-last-name";
    public static final String PERSON_VARIABLE = "record-person";
    public static final String DESCRIPTION_VARIABLE = "record-description";

    @Override
    public void define(WorkflowThread wf) {
        WfRunVariable firstName = wf.declareStr(FIRST_NAME_VARIABLE).required();
        WfRunVariable lastName = wf.declareStr(LAST_NAME_VARIABLE).required();
        WfRunVariable person = wf.declareStruct(PERSON_VARIABLE, RecordPerson.class);
        WfRunVariable description = wf.declareStr(DESCRIPTION_VARIABLE);

        person.assign(wf.execute(BUILD_RECORD_PERSON_TASK, firstName, lastName));
        description.assign(wf.execute(DESCRIBE_RECORD_PERSON_TASK, person));
    }
}

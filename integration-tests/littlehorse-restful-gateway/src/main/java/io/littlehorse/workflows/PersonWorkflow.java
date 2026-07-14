package io.littlehorse.workflows;

import static io.littlehorse.tasks.PersonTask.BUILD_PERSON_TASK;
import static io.littlehorse.tasks.PersonTask.DESCRIBE_PERSON_TASK;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.structs.Person;

@LHWorkflow(PersonWorkflow.PERSON_WORKFLOW)
public class PersonWorkflow implements LHWorkflowDefinition {

    public static final String PERSON_WORKFLOW = "person-wf";
    public static final String FIRST_NAME_VARIABLE = "first-name";
    public static final String LAST_NAME_VARIABLE = "last-name";
    public static final String PERSON_VARIABLE = "person";
    public static final String DESCRIPTION_VARIABLE = "description";

    @Override
    public void define(WorkflowThread wf) {
        WfRunVariable firstName = wf.declareStr(FIRST_NAME_VARIABLE).required();
        WfRunVariable lastName = wf.declareStr(LAST_NAME_VARIABLE).required();
        WfRunVariable person = wf.declareStruct(PERSON_VARIABLE, Person.class);
        WfRunVariable description = wf.declareStr(DESCRIPTION_VARIABLE);

        person.assign(wf.execute(BUILD_PERSON_TASK, firstName, lastName));
        description.assign(wf.execute(DESCRIBE_PERSON_TASK, person));
    }
}

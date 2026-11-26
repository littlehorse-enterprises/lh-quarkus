package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.structs.InlineStructDefAddress;
import io.littlehorse.structs.RecordPerson;

import java.util.Map;

@LHTask
public class RecordPersonTask {

    public static final String BUILD_RECORD_PERSON_TASK = "build-record-person";
    public static final String DESCRIBE_RECORD_PERSON_TASK = "describe-record-person";

    @LHTaskMethod(BUILD_RECORD_PERSON_TASK)
    public RecordPerson buildRecordPerson(String firstName, String lastName) {
        InlineStructDefAddress homeAddress =
                new InlineStructDefAddress("124 Sand Dune Lane", "Anchorhead");
        InlineStructDefAddress mailingAddress =
                new InlineStructDefAddress("1 Palace Plaza", "Theed");
        return new RecordPerson(
                firstName, lastName, homeAddress, Map.of("mailing", mailingAddress));
    }

    @LHTaskMethod(DESCRIBE_RECORD_PERSON_TASK)
    public String describeRecordPerson(RecordPerson person) {
        return "%s %s lives in %s and receives mail in %s"
                .formatted(
                        person.firstName(),
                        person.lastName(),
                        person.homeAddress().getCity(),
                        person.addressesByLabel().get("mailing").getCity());
    }
}

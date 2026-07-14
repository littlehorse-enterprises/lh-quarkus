package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.structs.Address;
import io.littlehorse.structs.Person;

@LHTask
public class PersonTask {

    public static final String BUILD_PERSON_TASK = "build-person";
    public static final String DESCRIBE_PERSON_TASK = "describe-person";

    @LHTaskMethod(BUILD_PERSON_TASK)
    public Person buildPerson(String firstName, String lastName) {
        return new Person(
                firstName,
                lastName,
                new Address(124, "Sand Dune Lane", "Anchorhead", "Tatooine", 97412));
    }

    @LHTaskMethod(DESCRIBE_PERSON_TASK)
    public String describePerson(Person person) {
        return "%s lives at %s".formatted(person, person.getHomeAddress());
    }
}

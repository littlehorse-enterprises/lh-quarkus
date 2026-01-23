package io.littlehorse.proxy.dev;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

import java.util.List;

@LHTask
public class JsonTask {

    public static final String RETURN_JSON_OBJECT = "return-json-object";
    public static final String RETURN_JSON_ARRAY = "return-json-array";
    public static final String RETURN_JSON_LIST = "return-json-list";

    public static class StarWarsCharacter {
        private String firstName;
        private String lastName;

        public StarWarsCharacter(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public static class StarWarsCharacter2 {
        private String firstName;
        private String lastName;

        public StarWarsCharacter2(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public static class StarWarsCharacter3 {
        private String firstName;
        private String lastName;

        public StarWarsCharacter3(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    @LHTaskMethod(RETURN_JSON_OBJECT)
    public StarWarsCharacter returnJsonObject() {
        return new StarWarsCharacter("Anakin", "Skywalker");
    }

    @LHTaskMethod(RETURN_JSON_ARRAY)
    public StarWarsCharacter2[] returnJsonArray() {
        return new StarWarsCharacter2[] {
            new StarWarsCharacter2("Luke", "Skywalker"), new StarWarsCharacter2("Leia", "Organa")
        };
    }

    @LHTaskMethod(RETURN_JSON_LIST)
    public List<StarWarsCharacter3> returnJsonList() {
        return List.of(
                new StarWarsCharacter3("Shmi", "Skywalker"),
                new StarWarsCharacter3("Padme", "Amidala"));
    }
}

package io.littlehorse.test;

import static io.littlehorse.workflows.JsonWorkflow.CHARACTER_VARIABLE;
import static io.littlehorse.workflows.JsonWorkflow.CHILDREN_VARIABLE;
import static io.littlehorse.workflows.JsonWorkflow.FAMILY_VARIABLE;
import static io.littlehorse.workflows.JsonWorkflow.UNBLOCK_JSON_WORKFLOW;
import static io.restassured.RestAssured.given;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.quarkus.rest.gateway.resource.externalevent.ExternalEventRequest;
import io.littlehorse.quarkus.rest.gateway.resource.wfrun.WfRunRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class RESTfulGatewayWfRunIT {

    @Test
    void shouldRunWf() {
        String wfName = "greetings";
        String id = UUID.randomUUID().toString();
        String expectedVariable = "myVariable";
        String expectedVariableName = "name";

        WfRunRequest request = new WfRunRequest(
                wfName, id, 0, 0, null, Map.of(expectedVariableName, expectedVariable));

        given().contentType(ContentType.JSON)
                .pathParam("tenant", "default")
                .body(request)
                .when()
                .post("/gateway/tenants/{tenant}/wf-runs")
                .then()
                .statusCode(200)
                .body("wfSpecId.name", is(wfName))
                .body("status", is("RUNNING"))
                .log()
                .all();

        await().atMost(Duration.ofSeconds(30))
                .pollDelay(Duration.ofMillis(500))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> given().contentType(ContentType.JSON)
                        .pathParam("tenant", "default")
                        .pathParam("id", id)
                        .when()
                        .get("/gateway/tenants/{tenant}/wf-runs/{id}")
                        .then()
                        .statusCode(200)
                        .body("wfSpecId.name", is(wfName))
                        .body("status", is("COMPLETED"))
                        .log()
                        .all());

        given().contentType(ContentType.JSON)
                .pathParam("tenant", "default")
                .pathParam("id", id)
                .when()
                .get("/gateway/tenants/{tenant}/wf-runs/{id}/variables")
                .then()
                .statusCode(200)
                .body("results[0].id.name", is(expectedVariableName))
                .body("results[0].value.str", is(expectedVariable))
                .body("results[0].wfSpecId.name", is(wfName))
                .log()
                .all();
    }

    @Test
    void shouldRunWfWithJsonVariables() {
        String wfName = "json";
        String wfRunId = UUID.randomUUID().toString();

        Map<String, Object> variables = Map.of(
                CHARACTER_VARIABLE, Map.of("firstName", "AnakinTest", "lastName", "Skywalker"),
                CHILDREN_VARIABLE,
                        new Map[] {
                            Map.of("firstName", "LukeTest", "lastName", "Skywalker"),
                            Map.of("firstName", "LeiaTest", "lastName", "Organa")
                        },
                FAMILY_VARIABLE,
                        List.of(
                                Map.of("firstName", "ShmiTest", "lastName", "Skywalker"),
                                Map.of("firstName", "PadmeTest", "lastName", "Amidala")));

        WfRunRequest wfRunRequest = new WfRunRequest(wfName, wfRunId, 0, 0, null, variables);

        ExternalEventRequest externalEventRequest =
                new ExternalEventRequest(UNBLOCK_JSON_WORKFLOW, wfRunId, null, null, null, null);

        given().contentType(ContentType.JSON)
                .pathParam("tenant", "default")
                .body(wfRunRequest)
                .when()
                .post("/gateway/tenants/{tenant}/wf-runs")
                .then()
                .statusCode(200)
                .body("wfSpecId.name", is(wfName))
                .body("status", is("RUNNING"))
                .log()
                .all();

        given().contentType(ContentType.JSON)
                .pathParam("tenant", "default")
                .pathParam("id", wfRunId)
                .when()
                .get("/gateway/tenants/{tenant}/wf-runs/{id}/variables")
                .then()
                .statusCode(200)
                .body("results[0].id.name", is("character"))
                .body("results[0].value.jsonObj", containsString("\"firstName\":\"AnakinTest\""))
                .body("results[1].id.name", is("children"))
                .body("results[1].value.jsonArr", containsString("\"firstName\":\"LukeTest\""))
                .body("results[1].value.jsonArr", containsString("\"firstName\":\"LeiaTest\""))
                .body("results[2].id.name", is("family"))
                .body("results[2].value.jsonArr", containsString("\"firstName\":\"ShmiTest\""))
                .body("results[2].value.jsonArr", containsString("\"firstName\":\"PadmeTest\""))
                .log()
                .all();

        given().contentType(ContentType.JSON)
                .pathParam("tenant", "default")
                .body(externalEventRequest)
                .when()
                .post("/gateway/tenants/{tenant}/external-events")
                .then()
                .statusCode(200)
                .body("id.wfRunId.id", is(wfRunId))
                .body("id.externalEventDefId.name", is(UNBLOCK_JSON_WORKFLOW))
                .body("claimed", is(true))
                .log()
                .all();

        await().atMost(Duration.ofSeconds(30))
                .pollDelay(Duration.ofMillis(500))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> given().contentType(ContentType.JSON)
                        .pathParam("tenant", "default")
                        .pathParam("id", wfRunId)
                        .when()
                        .get("/gateway/tenants/{tenant}/wf-runs/{id}")
                        .then()
                        .statusCode(200)
                        .body("wfSpecId.name", is(wfName))
                        .body("status", is("COMPLETED"))
                        .log()
                        .all());

        given().contentType(ContentType.JSON)
                .pathParam("tenant", "default")
                .pathParam("id", wfRunId)
                .when()
                .get("/gateway/tenants/{tenant}/wf-runs/{id}/variables")
                .then()
                .statusCode(200)
                .body("results[0].id.name", is("character"))
                .body(
                        "results[0].value.jsonObj",
                        is("{\"firstName\":\"Anakin\",\"lastName\":\"Skywalker\"}"))
                .body("results[1].id.name", is("children"))
                .body(
                        "results[1].value.jsonArr",
                        is(
                                "[{\"firstName\":\"Luke\",\"lastName\":\"Skywalker\"},{\"firstName\":\"Leia\",\"lastName\":\"Organa\"}]"))
                .body("results[2].id.name", is("family"))
                .body(
                        "results[2].value.jsonArr",
                        is(
                                "[{\"firstName\":\"Shmi\",\"lastName\":\"Skywalker\"},{\"firstName\":\"Padme\",\"lastName\":\"Amidala\"}]"))
                .log()
                .all();
    }

    @Test
    void shouldNotFoundWfRun() {
        WfRunRequest request = new WfRunRequest("not-a-wf", null, null, null, null, null);
        given().contentType(ContentType.JSON)
                .pathParam("tenant", "default")
                .body(request)
                .when()
                .post("/gateway/tenants/{tenant}/wf-runs")
                .then()
                .statusCode(404)
                .log()
                .all();
    }
}

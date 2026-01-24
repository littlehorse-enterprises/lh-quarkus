package io.littlehorse.test;

import static io.restassured.RestAssured.given;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.quarkus.rest.gateway.resource.wfrun.WfRunRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import java.time.Duration;
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

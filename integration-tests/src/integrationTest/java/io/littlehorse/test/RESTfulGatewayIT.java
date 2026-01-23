package io.littlehorse.test;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import io.littlehorse.common.ContainersTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class RESTfulGatewayIT {

    private static final String LH_VERSION = System.getProperty("lhVersion", "latest");

    @Test
    void shouldGetServerVersion() {
        given().when()
                .get("/gateway/version")
                .then()
                .statusCode(200)
                .body("version", is(LH_VERSION))
                .log()
                .all();
    }

    @Test
    void shouldGetWfSpecFromWfSpecName() {
        String wfSpecName = "greetings";
        given().pathParam("tenant", "default")
                .pathParam("wfSpecId", wfSpecName)
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs/{wfSpecId}")
                .then()
                .statusCode(200)
                .body("id.name", is(wfSpecName))
                .body("status", is("ACTIVE"))
                .log()
                .all();
    }

    @Test
    void shouldNotFoundWfSpec() {
        given().pathParam("tenant", "default")
                .pathParam("wfSpecId", "not-a-workflow")
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs/{wfSpecId}")
                .then()
                .statusCode(404)
                .log()
                .all();
    }

    @Test
    void shouldGetWfSpecFromWfSpecNameAndVersion() {
        String wfSpecName = "greetings";
        String version = "0.0";
        given().pathParam("tenant", "default")
                .pathParam("wfSpecId", wfSpecName)
                .pathParam("version", version)
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs/{wfSpecId}/versions/{version}")
                .then()
                .statusCode(200)
                .body("id.name", is(wfSpecName))
                .body("status", is("ACTIVE"))
                .log()
                .all();
    }

    @Test
    void shouldSearchAllWfSpec() {
        given().pathParam("tenant", "default")
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs")
                .then()
                .statusCode(200)
                .body("results", hasSize(2))
                .body("bookmark", is(nullValue()))
                .body("results[0].name", is("greetings"))
                .body("results[1].name", is("json"))
                .log()
                .all();
    }

    @Test
    void shouldSearchWfSpecWithBookmark() {
        Response getFirstObject = given().pathParam("tenant", "default")
                .queryParam("limit", 1)
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs");
        String bookmark = getFirstObject.jsonPath().getString("bookmark");

        getFirstObject
                .then()
                .statusCode(200)
                .body("results", hasSize(1))
                .body("results[0].name", is("greetings"))
                .body("bookmark", is(notNullValue()))
                .log()
                .all();

        given().pathParam("tenant", "default")
                .queryParam("limit", 1)
                .queryParam("bookmark", bookmark)
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs")
                .then()
                .statusCode(200)
                .body("results", hasSize(1))
                .body("bookmark", is(nullValue()))
                .body("results[0].name", is("json"))
                .log()
                .all();
    }
}

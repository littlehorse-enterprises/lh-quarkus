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
class RESTfulGatewayWfSpecTest {

    @Test
    void shouldGetWfSpecFromWfSpecName() {
        String name = "greetings";
        given().pathParam("tenant", "default")
                .pathParam("name", name)
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs/{name}")
                .then()
                .statusCode(200)
                .body("id.name", is(name))
                .body("status", is("ACTIVE"))
                .log()
                .all();
    }

    @Test
    void shouldNotFoundWfSpec() {
        given().pathParam("tenant", "default")
                .pathParam("name", "not-a-workflow")
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs/{name}")
                .then()
                .statusCode(404)
                .log()
                .all();
    }

    @Test
    void shouldGetWfSpecFromWfSpecNameAndVersion() {
        String name = "greetings";
        String version = "0.0";
        given().pathParam("tenant", "default")
                .pathParam("name", name)
                .pathParam("version", version)
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs/{name}/versions/{version}")
                .then()
                .statusCode(200)
                .body("id.name", is(name))
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
                .body("results", hasSize(4))
                .body("bookmark", is(nullValue()))
                .body("results[0].name", is("example-type-adapter"))
                .body("results[1].name", is("greetings"))
                .body("results[2].name", is("json"))
                .body("results[3].name", is("workflow-in-a-bean"))
                .log()
                .all();
    }

    @Test
    void shouldSearchWfSpecWithBookmark() {
        int limit = 2;
        Response getFirstObject = given().pathParam("tenant", "default")
                .queryParam("limit", limit)
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs");
        String bookmark = getFirstObject.jsonPath().getString("bookmark");

        getFirstObject
                .then()
                .statusCode(200)
                .body("results", hasSize(limit))
                .body("results[0].name", is("example-type-adapter"))
                .body("bookmark", is(notNullValue()))
                .log()
                .all();

        given().pathParam("tenant", "default")
                .queryParam("limit", limit)
                .queryParam("bookmark", bookmark)
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs")
                .then()
                .statusCode(200)
                .body("results", hasSize(2))
                .body("bookmark", is(nullValue()))
                .body("results[0].name", is("json"))
                .body("results[1].name", is("workflow-in-a-bean"))
                .log()
                .all();
    }
}

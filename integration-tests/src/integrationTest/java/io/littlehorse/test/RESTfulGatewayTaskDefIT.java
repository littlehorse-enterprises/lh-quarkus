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
class RESTfulGatewayTaskDefIT {

    @Test
    void shouldGetTaskDefFromTaskDefName() {
        String name = "greetings";
        given().pathParam("tenant", "default")
                .pathParam("name", name)
                .when()
                .get("/gateway/tenants/{tenant}/task-defs/{name}")
                .then()
                .statusCode(200)
                .body("id.name", is(name))
                .log()
                .all();
    }

    @Test
    void shouldNotFoundTaskDef() {
        given().pathParam("tenant", "default")
                .pathParam("name", "not-a-task")
                .when()
                .get("/gateway/tenants/{tenant}/task-defs/{name}")
                .then()
                .statusCode(404)
                .log()
                .all();
    }

    @Test
    void shouldSearchAllTaskDef() {
        given().pathParam("tenant", "default")
                .when()
                .get("/gateway/tenants/{tenant}/task-defs")
                .then()
                .statusCode(200)
                .body("results", hasSize(5))
                .body("bookmark", is(nullValue()))
                .body("results[0].name", is("greetings"))
                .body("results[1].name", is("print"))
                .body("results[2].name", is("return-json-array"))
                .body("results[3].name", is("return-json-list"))
                .body("results[4].name", is("return-json-object"))
                .log()
                .all();
    }

    @Test
    void shouldSearchTaskDefWithBookmark() {
        Response getFirstObject = given().pathParam("tenant", "default")
                .queryParam("limit", 1)
                .when()
                .get("/gateway/tenants/{tenant}/task-defs");
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
                .get("/gateway/tenants/{tenant}/task-defs")
                .then()
                .statusCode(200)
                .body("results", hasSize(1))
                .body("bookmark", is(notNullValue()))
                .body("results[0].name", is("print"))
                .log()
                .all();
    }
}

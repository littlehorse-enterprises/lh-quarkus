package io.littlehorse;

import static io.restassured.RestAssured.given;

import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(ContainersTestResource.class)
class GreetingResourceTest {

    @Test
    void testHelloEndpoint() {
        given().queryParam("name", "Anakin")
                .when()
                .get("/hello")
                .then()
                .statusCode(200)
                .body(is("Hello there! Anakin"));
    }
}

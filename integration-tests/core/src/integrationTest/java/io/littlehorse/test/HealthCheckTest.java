package io.littlehorse.test;

import static io.restassured.RestAssured.given;

import static org.hamcrest.CoreMatchers.hasItems;

import io.littlehorse.common.ContainersTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class HealthCheckTest {

    @Test
    void shouldGetStatusUp() {
        given().when()
                .get("/q/health")
                .then()
                .statusCode(200)
                .body("checks.name", hasItems("LH Tasks", "LH Server"))
                .body("checks.status", hasItems("UP", "UP"));
    }
}

package io.littlehorse.test;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.is;

import io.littlehorse.common.ContainersTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class RESTfulGatewayServerInformationIT {

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
}

package io.littlehorse.test;

import static io.restassured.RestAssured.given;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;

import io.littlehorse.common.ContainersTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;

/**
 * Verifies that the {@code LittleHorseReactiveStub} bean produced by the extension works end to end
 * through a REST resource that queries a registered {@code WfSpec}.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class ReactiveStubTest {

    @Test
    void shouldQueryWfSpecUsingReactiveStub() {
        await().atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofMillis(500))
                .ignoreExceptions()
                .untilAsserted(() -> given().pathParam("name", "greetings")
                        .when()
                        .get("/reactive/wf-specs/{name}")
                        .then()
                        .statusCode(200)
                        .body(is("greetings")));
    }
}

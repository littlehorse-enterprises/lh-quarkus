package io.littlehorse;

import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.CoreMatchers.is;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.LHStatus;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc;
import io.littlehorse.sdk.common.proto.WfRun;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.UUID;

@QuarkusTest
@QuarkusTestResource(ContainersTestResource.class)
class GreetingsResourceTest {

    @Inject
    LittleHorseGrpc.LittleHorseBlockingStub blockingStub;

    String expectedId;

    @BeforeEach
    void setUp() {
        expectedId = UUID.randomUUID().toString();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/hello", "/hello/reactive"})
    void testHelloEndpoint(String path) {
        given().queryParam("id", expectedId)
                .queryParam("name", "Anakin")
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .body(is(expectedId));

        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    WfRun result = blockingStub.getWfRun(LHLibUtil.wfRunIdFromString(expectedId));
                    assertThat(result.getId().getId()).isEqualTo(expectedId);
                    assertThat(result.getStatus()).isEqualTo(LHStatus.COMPLETED);
                });
    }
}

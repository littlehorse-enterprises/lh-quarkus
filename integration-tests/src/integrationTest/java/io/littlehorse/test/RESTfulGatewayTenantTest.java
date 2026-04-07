package io.littlehorse.test;

import static io.restassured.RestAssured.given;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.auth.TenantMetadataProvider;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc;
import io.littlehorse.sdk.common.proto.PutTenantRequest;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import net.datafaker.Faker;

import org.junit.jupiter.api.Test;

import java.time.Duration;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class RESTfulGatewayTenantTest {

    Faker faker = new Faker();

    @InjectLittleHorseBlockingStub
    LittleHorseGrpc.LittleHorseBlockingStub blockingStub;

    @Test
    void shouldGetWfSpecFromTenant() {
        // create tenant
        String tenant = faker.internet().domainWord();
        String wfName = faker.internet().domainWord();
        registerTenantAndWf(tenant, wfName);

        // test
        given().pathParam("tenant", tenant)
                .pathParam("name", wfName)
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs/{name}")
                .then()
                .statusCode(200)
                .body("id.name", is(wfName))
                .body("status", is("ACTIVE"))
                .log()
                .all();
    }

    @Test
    void shouldSearchAllWfSpecFromTenant() {
        // create tenant
        String tenant = faker.internet().domainWord();
        String wfName = faker.internet().domainWord();
        registerTenantAndWf(tenant, wfName);

        // test
        given().pathParam("tenant", tenant)
                .when()
                .get("/gateway/tenants/{tenant}/wf-specs")
                .then()
                .statusCode(200)
                .body("results", hasSize(1))
                .body("bookmark", is(nullValue()))
                .body("results[0].name", is(wfName))
                .log()
                .all();
    }

    private void registerTenantAndWf(String tenant, String wfName) {
        blockingStub.putTenant(PutTenantRequest.newBuilder().setId(tenant).build());
        Workflow wf = Workflow.newWorkflow(wfName, thread -> {});
        await().atMost(Duration.ofSeconds(30)).until(() -> {
            try {
                wf.registerWfSpec(
                        blockingStub.withCallCredentials(new TenantMetadataProvider(tenant)));
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }
}

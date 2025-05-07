package io.littlehorse;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class GreetingsResourceIT extends GreetingsResourceTest {
    // Execute the same tests but in packaged mode.
}

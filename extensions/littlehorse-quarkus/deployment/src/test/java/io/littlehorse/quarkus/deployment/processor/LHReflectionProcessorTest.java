package io.littlehorse.quarkus.deployment.processor;

import static org.assertj.core.api.Assertions.assertThat;

import io.littlehorse.sdk.worker.LHStructDef;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;

import org.jboss.jandex.Index;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

class LHReflectionProcessorTest {

    @Test
    void shouldRegisterTaskAndStructBeanTypesRecursively() throws IOException {
        Index index = Index.of(
                InlineTask.class, InlineCustomer.class, InlineAddress.class, NamedEnvelope.class);

        assertThat(LHReflectionProcessor.reflectiveTypeNames(index))
                .containsExactlyInAnyOrder(
                        InlineCustomer.class.getName(),
                        InlineAddress.class.getName(),
                        NamedEnvelope.class.getName())
                .doesNotContain(String.class.getName(), Map.class.getName());
    }

    static class InlineTask {

        @LHTaskMethod("normalize")
        @LHType(isInlineStruct = true)
        InlineCustomer normalize(
                @LHType(isInlineStruct = true) InlineCustomer customer,
                @LHType(isInlineStruct = true) InlineAddress inputOnlyAddress) {
            return customer;
        }
    }

    static class InlineCustomer {
        private InlineAddress address;
        private InlineAddress[] previousAddresses;
        private Map<String, InlineAddress> addressesByLabel;

        public InlineAddress getAddress() {
            return address;
        }

        public InlineAddress[] getPreviousAddresses() {
            return previousAddresses;
        }

        public Map<String, InlineAddress> getAddressesByLabel() {
            return addressesByLabel;
        }
    }

    static class InlineAddress {
        private InlineCustomer customer;

        public InlineCustomer getCustomer() {
            return customer;
        }
    }

    @LHStructDef("envelope")
    static class NamedEnvelope {
        private InlineAddress address;

        public InlineAddress getAddress() {
            return address;
        }
    }
}

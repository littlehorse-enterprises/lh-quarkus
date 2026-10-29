package io.littlehorse.quarkus.deployment.processor;

import static org.assertj.core.api.Assertions.assertThat;

import io.littlehorse.quarkus.deployment.item.LHWorkflowBuildItem;
import io.littlehorse.quarkus.workflow.LHReflectiveType;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHStructDef;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

import org.jboss.jandex.Index;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class LHReflectionProcessorTest {

    @Test
    void shouldRegisterTypesResolvedByWorkflowCompilation() {
        Class<?> customerType = WorkflowCustomer.class;
        Workflow workflow = Workflow.newWorkflow("reflection-test", thread -> {
            thread.declareInlineStruct("customer", customerType);
            thread.declareArray("customers", customerType);
            thread.declareMap("customers-by-name", String.class, customerType);
        });

        assertThat(LHReflectionProcessor.workflowReflectiveTypeNames(workflow))
                .containsExactlyInAnyOrder(
                        WorkflowCustomer.class.getName(), WorkflowOnlyAddress.class.getName());
    }

    @Test
    void shouldRegisterTypesFromChildThreadsHandlersAndEvents() {
        Workflow workflow = Workflow.newWorkflow("thread-types", thread -> {
            thread.spawnThread(
                    child -> child.declareInlineStruct("child", ChildType.class),
                    "child",
                    Map.of());
            thread.registerInterruptHandler(
                            "interrupt",
                            handler -> handler.declareInlineStruct("handler", HandlerType.class))
                    .withEventType(EventType.class);
            thread.waitForEvent("external-event").registeredAs(EventType.class);
            thread.throwEvent("workflow-event", "payload").registeredAs(EventType.class);
        });

        assertThat(LHReflectionProcessor.workflowReflectiveTypeNames(workflow))
                .containsExactlyInAnyOrder(
                        ChildType.class.getName(),
                        HandlerType.class.getName(),
                        EventType.class.getName());
    }

    @Test
    void shouldRegisterTypesFromWorkflowDefinition() throws IOException {
        assertWorkflowTypes(DefinitionWorkflow.class);
    }

    @Test
    void shouldRegisterTypesFromAnnotatedMethod() throws IOException {
        assertWorkflowTypes(MethodWorkflow.class);
    }

    @Test
    void shouldRegisterTypesFromStaticMethodWithoutConstructingBean() throws IOException {
        assertWorkflowTypes(StaticWorkflow.class);
    }

    @Test
    void shouldContinueDiscoveryWhenAnotherWorkflowRequiresRuntimeInjection() throws IOException {
        assertWorkflowTypes(InjectedWorkflow.class, MethodWorkflow.class);
    }

    private static void assertWorkflowTypes(Class<?>... workflowClasses) throws IOException {
        Index index = Index.of(workflowClasses);
        BeanArchiveIndexBuildItem indexBuildItem =
                new BeanArchiveIndexBuildItem(index, index, Set.of());
        List<LHWorkflowBuildItem> workflows = new ArrayList<>();
        LHServiceProcessor serviceProcessor = new LHServiceProcessor();
        serviceProcessor.scanLHWorkflowDefinition(workflows::add, indexBuildItem);
        serviceProcessor.scanLHWorkflowFromMethod(workflows::add, indexBuildItem);
        List<ReflectiveClassBuildItem> reflection = new ArrayList<>();

        new LHReflectionProcessor()
                .registerWorkflowReferencedTypes(reflection::add, workflows, Map.of());

        assertThat(reflection.stream().flatMap(item -> item.getClassNames().stream()))
                .containsExactlyInAnyOrder(
                        WorkflowCustomer.class.getName(), WorkflowOnlyAddress.class.getName());
        assertThat(reflection).allSatisfy(item -> {
            assertThat(item.isConstructors()).isTrue();
            assertThat(item.isMethods()).isTrue();
            assertThat(item.isFields()).isTrue();
        });
    }

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

    @Test
    void shouldRegisterAnnotatedWorkflowTypeRecursively() throws IOException {
        Index index = Index.of(WorkflowOnlyCustomer.class, WorkflowOnlyAddress.class);

        assertThat(LHReflectionProcessor.reflectiveTypeNames(index))
                .containsExactlyInAnyOrder(
                        WorkflowOnlyCustomer.class.getName(), WorkflowOnlyAddress.class.getName())
                .doesNotContain(String.class.getName());
    }

    public static class ChildType {}

    public static class HandlerType {}

    public static class EventType {}

    @LHWorkflow("definition-workflow")
    public static class DefinitionWorkflow implements LHWorkflowDefinition {
        @Override
        public void define(WorkflowThread thread) {
            Class<?> customerType = customerType();
            thread.declareInlineStruct("customer", customerType);
            thread.declareArray("customers", customerType);
            thread.declareMap("customers-by-name", String.class, customerType);
        }

        private Class<?> customerType() {
            return WorkflowCustomer.class;
        }
    }

    public static class MethodWorkflow {
        @LHWorkflow("method-workflow")
        public void define(WorkflowThread thread) {
            new DefinitionWorkflow().define(thread);
        }
    }

    public static class StaticWorkflow {
        public StaticWorkflow(String dependency) {
            throw new IllegalStateException("Must not construct a static workflow's bean");
        }

        @LHWorkflow("static-workflow")
        public static void define(WorkflowThread thread) {
            new DefinitionWorkflow().define(thread);
        }
    }

    @LHWorkflow("injected-workflow")
    public static class InjectedWorkflow implements LHWorkflowDefinition {
        public InjectedWorkflow(String dependency) {}

        @Override
        public void define(WorkflowThread thread) {
            throw new IllegalStateException("Requires runtime injection");
        }
    }

    public static class WorkflowCustomer {
        private WorkflowOnlyAddress address;

        public WorkflowOnlyAddress getAddress() {
            return address;
        }

        public void setAddress(WorkflowOnlyAddress address) {
            this.address = address;
        }
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

    @LHReflectiveType
    static class WorkflowOnlyCustomer {
        private WorkflowOnlyAddress address;

        public WorkflowOnlyAddress getAddress() {
            return address;
        }
    }

    public static class WorkflowOnlyAddress {
        private String street;

        public String getStreet() {
            return street;
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

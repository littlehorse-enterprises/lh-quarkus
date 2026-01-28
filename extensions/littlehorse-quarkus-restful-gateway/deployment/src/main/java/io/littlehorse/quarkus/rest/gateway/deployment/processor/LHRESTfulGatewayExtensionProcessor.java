package io.littlehorse.quarkus.rest.gateway.deployment.processor;

import io.littlehorse.quarkus.rest.gateway.context.TenantContext;
import io.littlehorse.quarkus.rest.gateway.infrastructure.GrpcExceptionMapper;
import io.littlehorse.quarkus.rest.gateway.infrastructure.ProtobufObjectMapperCustomizer;
import io.littlehorse.quarkus.rest.gateway.resource.externalevent.ExternalEventRepository;
import io.littlehorse.quarkus.rest.gateway.resource.externalevent.ExternalEventResource;
import io.littlehorse.quarkus.rest.gateway.resource.server.ServerInformationRepository;
import io.littlehorse.quarkus.rest.gateway.resource.server.ServerInformationResource;
import io.littlehorse.quarkus.rest.gateway.resource.task.TaskDefRepository;
import io.littlehorse.quarkus.rest.gateway.resource.task.TaskDefResource;
import io.littlehorse.quarkus.rest.gateway.resource.wfrun.WfRunRepository;
import io.littlehorse.quarkus.rest.gateway.resource.wfrun.WfRunResource;
import io.littlehorse.quarkus.rest.gateway.resource.wfspec.WfSpecRepository;
import io.littlehorse.quarkus.rest.gateway.resource.wfspec.WfSpecResource;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class LHRESTfulGatewayExtensionProcessor {

    @BuildStep
    FeatureBuildItem setFeatureName() {
        return new FeatureBuildItem("littlehorse-quarkus-restful-gateway");
    }

    @BuildStep
    AdditionalIndexedClassesBuildItem exposeResources() {
        return new AdditionalIndexedClassesBuildItem(
                ServerInformationResource.class.getName(),
                TaskDefResource.class.getName(),
                WfSpecResource.class.getName(),
                WfRunResource.class.getName(),
                ExternalEventResource.class.getName(),
                GrpcExceptionMapper.class.getName());
    }

    @BuildStep
    AdditionalBeanBuildItem exposeBeans() {
        return new AdditionalBeanBuildItem(
                ServerInformationResource.class,
                ServerInformationRepository.class,
                TenantContext.class,
                ProtobufObjectMapperCustomizer.class,
                GrpcExceptionMapper.class,
                WfSpecResource.class,
                WfSpecRepository.class,
                TaskDefResource.class,
                TaskDefRepository.class,
                WfRunRepository.class,
                WfRunResource.class,
                ExternalEventResource.class,
                ExternalEventRepository.class);
    }
}

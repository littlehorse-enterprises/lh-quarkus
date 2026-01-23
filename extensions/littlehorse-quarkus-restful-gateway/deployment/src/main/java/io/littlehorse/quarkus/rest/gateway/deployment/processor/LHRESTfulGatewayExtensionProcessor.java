package io.littlehorse.quarkus.rest.gateway.deployment.processor;

import io.littlehorse.quarkus.rest.gateway.context.TenantContext;
import io.littlehorse.quarkus.rest.gateway.infrastructure.GrpcExceptionMapper;
import io.littlehorse.quarkus.rest.gateway.infrastructure.ProtobufObjectMapperCustomizer;
import io.littlehorse.quarkus.rest.gateway.resource.server.ServerInformationResource;
import io.littlehorse.quarkus.rest.gateway.resource.server.ServerInformationService;
import io.littlehorse.quarkus.rest.gateway.resource.wfspec.WfSpecResource;
import io.littlehorse.quarkus.rest.gateway.resource.wfspec.WfSpecService;
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
                WfSpecResource.class.getName(),
                GrpcExceptionMapper.class.getName());
    }

    @BuildStep
    AdditionalBeanBuildItem exposeBeans() {
        return new AdditionalBeanBuildItem(
                ServerInformationResource.class,
                ServerInformationService.class,
                TenantContext.class,
                ProtobufObjectMapperCustomizer.class,
                GrpcExceptionMapper.class,
                WfSpecResource.class,
                WfSpecService.class);
    }
}

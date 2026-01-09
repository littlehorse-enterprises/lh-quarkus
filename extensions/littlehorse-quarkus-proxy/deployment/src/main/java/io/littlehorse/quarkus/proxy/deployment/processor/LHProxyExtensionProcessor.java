package io.littlehorse.quarkus.proxy.deployment.processor;

import io.littlehorse.quarkus.proxy.context.TenantContext;
import io.littlehorse.quarkus.proxy.infrastructure.GrpcExceptionMapper;
import io.littlehorse.quarkus.proxy.infrastructure.ProtobufObjectMapperCustomizer;
import io.littlehorse.quarkus.proxy.resource.server.ServerInformationResource;
import io.littlehorse.quarkus.proxy.resource.server.ServerInformationService;
import io.littlehorse.quarkus.proxy.resource.wfspec.WfSpecResource;
import io.littlehorse.quarkus.proxy.resource.wfspec.WfSpecService;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class LHProxyExtensionProcessor {

    @BuildStep
    FeatureBuildItem setFeatureName() {
        return new FeatureBuildItem("littlehorse-quarkus-proxy");
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

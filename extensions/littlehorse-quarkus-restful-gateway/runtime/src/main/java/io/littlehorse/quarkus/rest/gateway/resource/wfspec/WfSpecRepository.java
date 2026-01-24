package io.littlehorse.quarkus.rest.gateway.resource.wfspec;

import io.littlehorse.quarkus.rest.gateway.context.TenantContext;
import io.littlehorse.quarkus.rest.gateway.protobuf.ByteStringUtils;
import io.littlehorse.sdk.common.proto.GetLatestWfSpecRequest;
import io.littlehorse.sdk.common.proto.SearchWfSpecRequest;
import io.littlehorse.sdk.common.proto.WfSpec;
import io.littlehorse.sdk.common.proto.WfSpecId;
import io.littlehorse.sdk.common.proto.WfSpecIdList;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class WfSpecRepository {

    private final TenantContext context;

    public WfSpecRepository(TenantContext context) {
        this.context = context;
    }

    public Uni<WfSpec> get(String name) {
        GetLatestWfSpecRequest request =
                GetLatestWfSpecRequest.newBuilder().setName(name).build();
        return context.getLittleHorseReactiveStub().getLatestWfSpec(request);
    }

    public Uni<WfSpec> get(String name, String version) {
        WorkflowVersion workflowVersion = WorkflowVersion.of(version);
        WfSpecId request = WfSpecId.newBuilder()
                .setName(name)
                .setMajorVersion(workflowVersion.getMajorVersion())
                .setRevision(workflowVersion.getRevision())
                .build();
        return context.getLittleHorseReactiveStub().getWfSpec(request);
    }

    public Uni<WfSpecIdList> search(String prefix, String bookmark, Integer limit) {
        SearchWfSpecRequest.Builder requestBuilder = SearchWfSpecRequest.newBuilder();

        if (StringUtils.isNotBlank(prefix)) {
            requestBuilder.setPrefix(prefix);
        }

        if (StringUtils.isNotBlank(bookmark)) {
            try {
                requestBuilder.setBookmark(ByteStringUtils.stringToByteString(bookmark));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid bookmark format", e);
            }
        }

        if (limit != null) {
            requestBuilder.setLimit(limit);
        }

        return context.getLittleHorseReactiveStub().searchWfSpec(requestBuilder.build());
    }
}

package io.littlehorse.quarkus.rest.gateway.resource.task;

import io.littlehorse.quarkus.rest.gateway.context.TenantContext;
import io.littlehorse.quarkus.rest.gateway.protobuf.ByteStringUtils;
import io.littlehorse.sdk.common.proto.SearchTaskDefRequest;
import io.littlehorse.sdk.common.proto.TaskDef;
import io.littlehorse.sdk.common.proto.TaskDefId;
import io.littlehorse.sdk.common.proto.TaskDefIdList;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class TaskDefRepository {

    private final TenantContext context;

    public TaskDefRepository(TenantContext context) {
        this.context = context;
    }

    public Uni<TaskDef> get(String name) {
        TaskDefId request = TaskDefId.newBuilder().setName(name).build();
        return context.getLittleHorseReactiveStub().getTaskDef(request);
    }

    public Uni<TaskDefIdList> search(String prefix, String bookmark, Integer limit) {
        SearchTaskDefRequest.Builder requestBuilder = SearchTaskDefRequest.newBuilder();

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

        return context.getLittleHorseReactiveStub().searchTaskDef(requestBuilder.build());
    }
}

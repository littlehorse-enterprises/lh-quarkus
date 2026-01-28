package io.littlehorse.quarkus.rest.gateway.resource.externalevent;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.ExternalEventDefId;
import io.littlehorse.sdk.common.proto.PutExternalEventRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import org.apache.commons.lang3.StringUtils;

public record ExternalEventRequest(
        @NotBlank(message = "ExternalEventDefName must not be blank")
        String externalEventDefName,

        @NotBlank(message = "WfRunId must not be blank") String wfRunId,
        String guid,

        @PositiveOrZero(message = "ThreadRunNumber has to be greater or equal to 0")
        Integer threadRunNumber,

        @PositiveOrZero(message = "NodeRunPosition has to be greater or equal to 0")
        Integer nodeRunPosition,

        Object content) {
    public PutExternalEventRequest toProtobuf() {
        PutExternalEventRequest.Builder builder = PutExternalEventRequest.newBuilder()
                .setWfRunId(LHLibUtil.wfRunIdFromString(wfRunId))
                .setExternalEventDefId(
                        ExternalEventDefId.newBuilder().setName(externalEventDefName));

        if (StringUtils.isNotBlank(guid)) {
            builder.setGuid(guid);
        }

        if (threadRunNumber != null) {
            builder.setThreadRunNumber(threadRunNumber);
        }

        if (nodeRunPosition != null) {
            builder.setNodeRunPosition(nodeRunPosition);
        }

        if (content != null) {
            builder.setContent(LHLibUtil.objToVarVal(content));
        }

        return builder.build();
    }
}

package io.littlehorse.quarkus.rest.gateway.resource.wfrun;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.RunWfRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public record WfRunRequest(
        @NotBlank(message = "WfSpec name must not be blank") String wfSpecName,

        String id,

        @PositiveOrZero(message = "Major version has to be greater or equal to 0")
        Integer majorVersion,

        @PositiveOrZero(message = "Revision has to be greater or equal to 0")
        Integer revision,

        String parentWfRunId,
        Map<String, Object> variables) {
    public RunWfRequest toProtobuf() {
        RunWfRequest.Builder builder = RunWfRequest.newBuilder().setWfSpecName(wfSpecName);

        if (StringUtils.isNotBlank(id)) {
            builder.setId(id);
        }

        if (majorVersion != null) {
            builder.setMajorVersion(majorVersion);
        }

        if (revision != null) {
            builder.setRevision(revision);
        }

        if (StringUtils.isNotBlank(parentWfRunId)) {
            builder.setParentWfRunId(LHLibUtil.wfRunIdFromString(parentWfRunId));
        }

        if (variables != null) {
            variables.forEach(
                    (key, value) -> builder.putVariables(key, LHLibUtil.objToVarVal(value)));
        }

        return builder.build();
    }
}

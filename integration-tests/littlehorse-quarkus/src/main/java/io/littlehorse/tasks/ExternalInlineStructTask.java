package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.common.proto.InlineStruct;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;

@LHTask
public class ExternalInlineStructTask {

    public static final String TASK_DEF_NAME = "external-inline-struct-test-task";
    public static final String PROMPT_STRUCT_DEF_NAME = "lh-external-prompt";
    public static final String REQUEST_STRUCT_DEF_NAME = "lh-external-request";
    public static final String RESPONSE_STRUCT_DEF_NAME = "lh-external-response";
    public static final String REQUEST_VARIABLE = "request";

    private static final String TASK_NAME_EXPRESSION = "${external.inline-struct.task.name}";
    private static final String REQUEST_STRUCT_EXPRESSION =
            "${external.inline-struct.request.name}";
    private static final String RESPONSE_STRUCT_EXPRESSION =
            "${external.inline-struct.response.name}";

    @LHTaskMethod(TASK_NAME_EXPRESSION)
    @LHType(structDefName = RESPONSE_STRUCT_EXPRESSION)
    public InlineStruct transform(
            @LHType(name = REQUEST_VARIABLE, structDefName = REQUEST_STRUCT_EXPRESSION)
                    InlineStruct request) {
        return InlineStruct.newBuilder()
                .putFields(
                        "response",
                        request.getFieldsOrThrow("prompt")
                                .getValue()
                                .getStruct()
                                .getStruct()
                                .getFieldsOrThrow("text"))
                .build();
    }
}

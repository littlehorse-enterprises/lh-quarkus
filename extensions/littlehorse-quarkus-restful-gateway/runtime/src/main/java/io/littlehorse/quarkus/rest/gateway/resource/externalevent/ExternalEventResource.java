package io.littlehorse.quarkus.rest.gateway.resource.externalevent;

import io.littlehorse.sdk.common.proto.ExternalEvent;
import io.smallrye.mutiny.Uni;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/gateway/tenants/{tenant}/external-events")
public class ExternalEventResource {

    public static final String POST_EXTERNALEVENT_EXAMPLE_RESPONSE = """
            {
              "claimed": true,
              "content": {},
              "createdAt": "2026-01-24T17:45:15.268Z",
              "id": {
                "wfRunId": {
                  "id": "fb7cfff0dda24f559af6daa8803c33f7"
                },
                "externalEventDefId": {
                  "name": "my-external-event"
                },
                "guid": "f369dd9031c8435f9b31332bd3beaf96"
              },
              "nodeRunPosition": 1,
              "threadRunNumber": 0
            }
            """;

    private final ExternalEventRepository repository;

    public ExternalEventResource(ExternalEventRepository repository) {
        this.repository = repository;
    }

    @POST
    @APIResponse(responseCode = "400", description = "Bad request")
    @APIResponse(responseCode = "404", description = "Record not found")
    @APIResponse(responseCode = "409", description = "Record already exists")
    @APIResponse(responseCode = "500", description = "Internal error")
    @APIResponse(
            responseCode = "200",
            description = "Record created successfully",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema =
                                    @Schema(
                                            examples = {POST_EXTERNALEVENT_EXAMPLE_RESPONSE},
                                            externalDocs =
                                                    @ExternalDocumentation(
                                                            url =
                                                                    "https://littlehorse.io/docs/server/api#externalevent"))))
    @Parameter(
            name = "tenant",
            in = ParameterIn.PATH,
            description = "Tenant name",
            schema = @Schema(type = SchemaType.STRING))
    @Tag(ref = "ExternalEvent")
    public Uni<ExternalEvent> post(@Valid ExternalEventRequest request) {
        return repository.post(request);
    }
}

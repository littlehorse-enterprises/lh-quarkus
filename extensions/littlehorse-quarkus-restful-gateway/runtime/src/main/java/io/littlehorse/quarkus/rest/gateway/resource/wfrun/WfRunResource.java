package io.littlehorse.quarkus.rest.gateway.resource.wfrun;

import io.littlehorse.sdk.common.proto.VariableList;
import io.littlehorse.sdk.common.proto.WfRun;
import io.smallrye.mutiny.Uni;

import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/gateway/tenants/{tenant}/wf-runs")
public class WfRunResource {

    public static final String POST_WFRUN_EXAMPLE_RESPONSE = """
            {
               "id": {
                 "id": "597bfc2a10c346f6a01a04a84c3b6ee6"
               },
               "wfSpecId": {
                 "name": "greetings",
                 "majorVersion": 0,
                 "revision": 0
               },
               "oldWfSpecVersions": [],
               "status": "RUNNING",
               "greatestThreadrunNumber": 0,
               "startTime": "2026-01-24T15:38:35.421Z",
               "threadRuns": [{
                 "wfSpecId": {
                   "name": "greetings",
                   "majorVersion": 0,
                   "revision": 0
                 },
                 "number": 0,
                 "status": "RUNNING",
                 "threadSpecName": "entrypoint",
                 "startTime": "2026-01-24T15:38:35.424Z",
                 "childThreadIds": [],
                 "haltReasons": [],
                 "currentNodePosition": 1,
                 "handledFailedChildren": [],
                 "type": "ENTRYPOINT"
               }],
               "pendingInterrupts": [],
               "pendingFailures": []
            }
            """;
    private static final String GET_VARIABLES_EXAMPLE_RESPONSE = """
            {
              "results": [{
                "id": {
                  "wfRunId": {
                    "id": "my-id"
                  },
                  "threadRunNumber": 0,
                  "name": "name"
                },
                "value": {
                  "str": "Anakin"
                },
                "createdAt": "2026-01-24T16:17:43.337Z",
                "wfSpecId": {
                  "name": "greetings",
                  "majorVersion": 0,
                  "revision": 0
                },
                "masked": false
              }]
            }
            """;

    private final WfRunRepository repository;

    public WfRunResource(WfRunRepository repository) {
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
                                            examples = {POST_WFRUN_EXAMPLE_RESPONSE},
                                            externalDocs =
                                                    @ExternalDocumentation(
                                                            url =
                                                                    "https://littlehorse.io/docs/server/api#wfrun"))))
    @Parameter(
            name = "tenant",
            in = ParameterIn.PATH,
            description = "Tenant name",
            schema = @Schema(type = SchemaType.STRING))
    @Tag(ref = "WfRun")
    public Uni<WfRun> run(@Valid WfRunRequest request) {
        return repository.run(request);
    }

    @GET
    @Path("/{id}/variables")
    @APIResponse(responseCode = "400", description = "Bad request")
    @APIResponse(responseCode = "404", description = "Record not found")
    @APIResponse(responseCode = "500", description = "Internal error")
    @APIResponse(
            responseCode = "200",
            description = "List retrieved successfully",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema =
                                    @Schema(
                                            examples = {GET_VARIABLES_EXAMPLE_RESPONSE},
                                            externalDocs =
                                                    @ExternalDocumentation(
                                                            url =
                                                                    "https://littlehorse.io/docs/server/api#variablelist"))))
    @Parameter(
            name = "tenant",
            in = ParameterIn.PATH,
            description = "Tenant name",
            schema = @Schema(type = SchemaType.STRING))
    @Tag(ref = "WfRun")
    public Uni<VariableList> variables(@PathParam("id") String id) {
        return repository.variables(id);
    }

    @GET
    @Path("/{id}")
    @APIResponse(responseCode = "400", description = "Bad request")
    @APIResponse(responseCode = "404", description = "Record not found")
    @APIResponse(responseCode = "500", description = "Internal error")
    @APIResponse(
            responseCode = "200",
            description = "Record retrieved successfully",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema =
                                    @Schema(
                                            examples = {POST_WFRUN_EXAMPLE_RESPONSE},
                                            externalDocs =
                                                    @ExternalDocumentation(
                                                            url =
                                                                    "https://littlehorse.io/docs/server/api#wfrun"))))
    @Parameter(
            name = "tenant",
            in = ParameterIn.PATH,
            description = "Tenant name",
            schema = @Schema(type = SchemaType.STRING))
    @Tag(ref = "WfRun")
    public Uni<WfRun> get(@PathParam("id") String id) {
        return repository.get(id);
    }
}

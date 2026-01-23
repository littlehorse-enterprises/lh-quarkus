package io.littlehorse.quarkus.rest.gateway.resource.wfspec;

import io.littlehorse.sdk.common.proto.WfSpec;
import io.littlehorse.sdk.common.proto.WfSpecIdList;
import io.smallrye.mutiny.Uni;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/gateway/tenants/{tenant}/wf-specs")
public class WfSpecResource {

    private final WfSpecService service;

    public WfSpecResource(WfSpecService service) {
        this.service = service;
    }

    @GET
    @Path("/{wfSpecId}")
    @APIResponses({
        @APIResponse(
                responseCode = "200",
                description = "Record retrieved successfully",
                content =
                        @Content(
                                mediaType = MediaType.APPLICATION_JSON,
                                schema =
                                        @Schema(
                                                examples = {"""
                                    {
                                      "id": {
                                        "name": "greetings",
                                        "majorVersion": 0,
                                        "revision": 0
                                      },
                                      "createdAt": "2026-01-20T16:10:06.917Z",
                                      "frozenVariables": [],
                                      "status": "ACTIVE",
                                      "threadSpecs": {
                                        "entrypoint": {
                                          "nodes": {
                                            "0-entrypoint-ENTRYPOINT": {
                                              "outgoingEdges": [{
                                                "sinkNodeName": "1-greetings-TASK",
                                                "variableMutations": []
                                              }],
                                              "failureHandlers": [],
                                              "entrypoint": {
                                              }
                                            },
                                            "1-greetings-TASK": {
                                              "outgoingEdges": [{
                                                "sinkNodeName": "2-exit-EXIT",
                                                "variableMutations": []
                                              }],
                                              "failureHandlers": [],
                                              "task": {
                                                "taskDefId": {
                                                  "name": "greetings"
                                                },
                                                "timeoutSeconds": 60,
                                                "retries": 0,
                                                "variables": [{
                                                  "variableName": "name"
                                                }]
                                              }
                                            },
                                            "2-exit-EXIT": {
                                              "outgoingEdges": [],
                                              "failureHandlers": [],
                                              "exit": {
                                              }
                                            }
                                          },
                                          "variableDefs": [{
                                            "varDef": {
                                              "name": "name",
                                              "typeDef": {
                                                "primitiveType": "STR",
                                                "masked": false
                                              }
                                            },
                                            "required": false,
                                            "searchable": false,
                                            "jsonIndexes": [],
                                            "accessLevel": "PRIVATE_VAR"
                                          }],
                                          "interruptDefs": []
                                        }
                                      },
                                      "entrypointThreadName": "entrypoint"
                                    }
                                    """},
                                                externalDocs =
                                                        @ExternalDocumentation(
                                                                url =
                                                                        "https://littlehorse.io/docs/server/api#wfspec"))))
    })
    @Parameters(
            value = {
                @Parameter(
                        name = "tenant",
                        in = ParameterIn.PATH,
                        description = "Tenant name",
                        schema = @Schema(type = SchemaType.STRING))
            })
    @Tag(ref = "WfSpec")
    public Uni<WfSpec> get(@PathParam("wfSpecId") String wfSpecId) {
        return service.get(wfSpecId);
    }

    @GET
    @APIResponses({
        @APIResponse(
                responseCode = "200",
                description = "List retrieved successfully",
                content =
                        @Content(
                                mediaType = MediaType.APPLICATION_JSON,
                                schema =
                                        @Schema(
                                                examples = {"""
                                            {
                                              "results": [{
                                                "name": "greetings",
                                                "majorVersion": 0,
                                                "revision": 0
                                              }],
                                              "bookmark": "ChgIABIUEhIyL3dmLTAvMDAwMDAvMDAwMDA="
                                            }
                                    """},
                                                externalDocs =
                                                        @ExternalDocumentation(
                                                                url =
                                                                        "https://littlehorse.io/docs/server/api#wfspecidlist"))))
    })
    @Parameters(
            value = {
                @Parameter(
                        name = "tenant",
                        in = ParameterIn.PATH,
                        description = "Tenant name",
                        schema = @Schema(type = SchemaType.STRING))
            })
    @Tag(ref = "WfSpec")
    public Uni<WfSpecIdList> search(
            @Parameter(description = "Maximum number of workflow specs to return")
                    @QueryParam("limit")
                    @Positive
                    @Max(100)
                    @DefaultValue("10")
                    Integer limit,
            @Parameter(description = "Search prefix for workflow names") @QueryParam("prefix")
                    String prefix,
            @Parameter(description = "Bookmark for pagination, it is a base64 string")
                    @QueryParam("bookmark")
                    String bookmark) {
        return service.search(prefix, bookmark, limit);
    }
}

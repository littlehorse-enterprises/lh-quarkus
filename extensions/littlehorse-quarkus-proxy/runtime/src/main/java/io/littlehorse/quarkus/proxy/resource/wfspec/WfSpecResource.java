package io.littlehorse.quarkus.proxy.resource.wfspec;

import io.littlehorse.sdk.common.proto.WfSpec;
import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/proxy/tenants/{tenant}/wf-specs")
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
                description = "WfSpec retrieved successfully",
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
    public Uni<WfSpec> get(@PathParam("wfSpecId") String wfSpecId) {
        return service.get(wfSpecId);
    }
}

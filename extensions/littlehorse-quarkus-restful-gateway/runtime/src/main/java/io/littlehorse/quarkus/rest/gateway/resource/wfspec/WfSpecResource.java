package io.littlehorse.quarkus.rest.gateway.resource.wfspec;

import io.littlehorse.sdk.common.proto.WfSpec;
import io.littlehorse.sdk.common.proto.WfSpecIdList;
import io.smallrye.mutiny.Uni;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/gateway/tenants/{tenant}/wf-specs")
public class WfSpecResource {

    public static final String SEARCH_WFSPEC_EXAMPLE_RESPONSE = """
            {
              "results": [{
                "name": "greetings",
                "majorVersion": 0,
                "revision": 0
              }],
              "bookmark": "ChgIABIUEhIyL3dmLTAvMDAwMDAvMDAwMDA="
            }
            """;
    public static final String GET_WFSPEC_EXAMPLE_RESPONSE = """
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
            """;
    private final WfSpecRepository repository;

    public WfSpecResource(WfSpecRepository repository) {
        this.repository = repository;
    }

    @GET
    @Path("/{name}")
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
                                            examples = {GET_WFSPEC_EXAMPLE_RESPONSE},
                                            externalDocs =
                                                    @ExternalDocumentation(
                                                            url =
                                                                    "https://littlehorse.io/docs/server/api#wfspec"))))
    @Parameter(
            name = "tenant",
            in = ParameterIn.PATH,
            description = "Tenant name",
            schema = @Schema(type = SchemaType.STRING))
    @Tag(ref = "WfSpec")
    public Uni<WfSpec> get(@PathParam("name") String name) {
        return repository.get(name);
    }

    @GET
    @Path("/{name}/versions/{version}")
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
                                            examples = {GET_WFSPEC_EXAMPLE_RESPONSE},
                                            externalDocs =
                                                    @ExternalDocumentation(
                                                            url =
                                                                    "https://littlehorse.io/docs/server/api#wfspec"))))
    @Parameter(
            name = "tenant",
            in = ParameterIn.PATH,
            description = "Tenant name",
            schema = @Schema(type = SchemaType.STRING))
    @Tag(ref = "WfSpec")
    public Uni<WfSpec> get(
            @PathParam("name") @NotBlank String name,
            @PathParam("version")
                    @NotBlank
                    @Pattern(
                            regexp = "\\d+\\.\\d+",
                            message = "Version should be in the format major.revision")
                    String version) {
        return repository.get(name, version);
    }

    @GET
    @APIResponse(responseCode = "400", description = "Bad request")
    @APIResponse(responseCode = "500", description = "Internal error")
    @APIResponse(
            responseCode = "200",
            description = "List retrieved successfully",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema =
                                    @Schema(
                                            examples = {SEARCH_WFSPEC_EXAMPLE_RESPONSE},
                                            externalDocs =
                                                    @ExternalDocumentation(
                                                            url =
                                                                    "https://littlehorse.io/docs/server/api#wfspecidlist"))))
    @Parameter(
            name = "tenant",
            in = ParameterIn.PATH,
            description = "Tenant name",
            schema = @Schema(type = SchemaType.STRING))
    @Tag(ref = "WfSpec")
    public Uni<WfSpecIdList> search(
            @Parameter(description = "Maximum number of records to return")
                    @QueryParam("limit")
                    @Positive
                    @Max(100)
                    @DefaultValue("10")
                    Integer limit,
            @Parameter(description = "Search by prefix") @QueryParam("prefix") String prefix,
            @Parameter(description = "Bookmark for pagination, it is a base64 string")
                    @QueryParam("bookmark")
                    String bookmark) {
        return repository.search(prefix, bookmark, limit);
    }
}

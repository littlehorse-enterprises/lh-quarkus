package io.littlehorse.quarkus.rest.gateway.resource.taskdef;

import io.littlehorse.sdk.common.proto.TaskDef;
import io.littlehorse.sdk.common.proto.TaskDefIdList;
import io.littlehorse.sdk.common.proto.TaskWorkerGroup;
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
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/gateway/tenants/{tenant}/task-defs")
public class TaskDefResource {

    public static final String SEARCH_TASKDEF_EXAMPLE_RESPONSE = """
            {
              "results": [{
                "name": "greetings"
              }],
              "bookmark": "Cg0IABIJEgcwL3ByaW50"
            }
            """;
    public static final String GET_TASKDEF_EXAMPLE_RESPONSE = """
            {
              "id": {
                "name": "greetings"
              },
              "inputVars": [{
                "name": "name",
                "typeDef": {
                  "primitiveType": "STR",
                  "masked": false
                }
              }],
              "createdAt": "2026-01-23T22:16:50.648Z",
              "returnType": {
                "returnType": {
                  "primitiveType": "STR",
                  "masked": false
                }
              }
            }
            """;
    public static final String GET_WORKER_GROUP_EXAMPLE_RESPONSE = """
            {
               "id":  {
                 "taskDefId":  {
                   "name":  "greetings"
                 }
               },
               "createdAt":  "2026-02-28T15:42:10.467Z",
               "taskWorkers":  {
                 "worker-0279f257b66a4cb885e854548ab6f131":  {
                   "taskWorkerId":  "worker-0279f257b66a4cb885e854548ab6f131",
                   "latestHeartbeat":  "2026-02-28T16:05:10.588Z",
                   "hosts":  [
                     {
                       "host":  "littlehorse",
                       "port":  2011
                     }
                   ]
                 }
               }
             }
            """;
    private final TaskDefRepository repository;

    public TaskDefResource(TaskDefRepository repository) {
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
                                            examples = {GET_TASKDEF_EXAMPLE_RESPONSE},
                                            externalDocs =
                                                    @ExternalDocumentation(
                                                            url =
                                                                    "https://littlehorse.io/docs/server/api#taskdef"))))
    @Parameter(
            name = "tenant",
            in = ParameterIn.PATH,
            description = "Tenant name",
            schema = @Schema(type = SchemaType.STRING))
    @Tag(ref = "TaskDef")
    public Uni<TaskDef> get(@PathParam("name") String name) {
        return repository.get(name);
    }

    @GET
    @Path("/{name}/workers")
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
                                            examples = {GET_WORKER_GROUP_EXAMPLE_RESPONSE},
                                            externalDocs =
                                                    @ExternalDocumentation(
                                                            url =
                                                                    "https://littlehorse.io/docs/server/api#taskworkergroup"))))
    @Parameter(
            name = "tenant",
            in = ParameterIn.PATH,
            description = "Tenant name",
            schema = @Schema(type = SchemaType.STRING))
    @Tag(ref = "TaskDef")
    public Uni<TaskWorkerGroup> getWorkers(@PathParam("name") String name) {
        return repository.getWorkers(name);
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
                                            examples = {SEARCH_TASKDEF_EXAMPLE_RESPONSE},
                                            externalDocs =
                                                    @ExternalDocumentation(
                                                            url =
                                                                    "https://littlehorse.io/docs/server/api#taskdefidlist"))))
    @Parameter(
            name = "tenant",
            in = ParameterIn.PATH,
            description = "Tenant name",
            schema = @Schema(type = SchemaType.STRING))
    @Tag(ref = "TaskDef")
    public Uni<TaskDefIdList> search(
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

package io.littlehorse.quarkus.rest.gateway.resource.server;

import io.smallrye.mutiny.Uni;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/gateway/version")
@RolesAllowed({
    "${quarkus.littlehorse.gateway.oauth2.rbac.admin-role}",
    "${quarkus.littlehorse.gateway.oauth2.rbac.reader-role}"
})
public class ServerInformationResource {

    private final ServerInformationRepository repository;

    public ServerInformationResource(ServerInformationRepository repository) {
        this.repository = repository;
    }

    @GET
    @APIResponse(responseCode = "200", description = "Record retrieved successfully")
    @APIResponse(responseCode = "500", description = "Internal error")
    @Tag(ref = "Version")
    public Uni<ServerInformationResponse> get() {
        return repository.get();
    }
}

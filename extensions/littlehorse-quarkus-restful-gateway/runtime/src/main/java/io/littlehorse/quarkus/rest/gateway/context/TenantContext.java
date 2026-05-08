package io.littlehorse.quarkus.rest.gateway.context;

import io.grpc.CallCredentials;
import io.grpc.CompositeCallCredentials;
import io.littlehorse.quarkus.reactive.LittleHorseReactiveStub;
import io.littlehorse.quarkus.rest.gateway.config.LHGatewayOAuth2Config;
import io.littlehorse.sdk.common.auth.TenantMetadataProvider;
import io.littlehorse.sdk.common.proto.TenantId;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import java.util.Optional;

@RequestScoped
public class TenantContext {
    private static final String TENANT_PATH = "tenant";

    // context objects https://quarkus.io/guides/rest#accessing-context-objects
    private final UriInfo uriInfo;
    private final HttpHeaders httpHeaders;
    private final LittleHorseReactiveStub stub;
    private final LHGatewayOAuth2Config oauth2Config;

    public TenantContext(
            UriInfo uriInfo,
            HttpHeaders httpHeaders,
            LittleHorseReactiveStub stub,
            LHGatewayOAuth2Config oauth2Config) {
        this.uriInfo = uriInfo;
        this.httpHeaders = httpHeaders;
        this.stub = stub;
        this.oauth2Config = oauth2Config;
    }

    public String getTenant() {
        return Optional.ofNullable(uriInfo.getPathParameters().getFirst(TENANT_PATH))
                .map(String::trim)
                .filter(tenant -> !tenant.isEmpty())
                .orElseThrow(() -> new BadRequestException("Tenant path parameter not found"));
    }

    public LittleHorseReactiveStub getLittleHorseReactiveStub() {
        return stub.withCallCredentials(buildCallCredentials());
    }

    private CallCredentials buildCallCredentials() {
        TenantMetadataProvider tenantProvider = new TenantMetadataProvider(getTenant());
        return switch (oauth2Config.oauth2Mode()) {
            case NONE, RBAC -> tenantProvider;
            case TOKEN_FORWARD ->
                Optional.ofNullable(httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION))
                        .map(token -> (CallCredentials) new CompositeCallCredentials(
                                tenantProvider, new TokenForwardingProvider(token)))
                        .orElse(tenantProvider);
        };
    }

    public TenantId getTenantId() {
        return TenantId.newBuilder().setId(getTenant()).build();
    }
}

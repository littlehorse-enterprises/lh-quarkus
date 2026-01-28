package io.littlehorse.quarkus.rest.gateway.context;

import io.grpc.CallCredentials;
import io.grpc.CompositeCallCredentials;
import io.littlehorse.quarkus.reactive.LittleHorseReactiveStub;
import io.littlehorse.sdk.common.auth.TenantMetadataProvider;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.TenantId;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import java.util.Optional;

@RequestScoped
public class TenantContext {
    private static final String TENANT_PATH = "tenant";
    private final LHConfig config;

    // context objects https://quarkus.io/guides/rest#accessing-context-objects
    private final UriInfo uriInfo;
    private final HttpHeaders httpHeaders;

    public TenantContext(LHConfig config, UriInfo uriInfo, HttpHeaders httpHeaders) {
        this.config = config;
        this.uriInfo = uriInfo;
        this.httpHeaders = httpHeaders;
    }

    public String getTenant() {
        return Optional.ofNullable(uriInfo.getPathParameters().getFirst(TENANT_PATH))
                .map(String::trim)
                .filter(tenant -> !tenant.isEmpty())
                .orElseThrow(() -> new BadRequestException("Tenant path parameter not found"));
    }

    public LittleHorseReactiveStub getLittleHorseReactiveStub() {
        return new LittleHorseReactiveStub(config.getFutureStub(
                        config.getApiBootstrapHost(), config.getApiBootstrapPort()))
                .withCallCredentials(getCredentials());
    }

    private Optional<String> getAuthorizationToken() {
        return Optional.ofNullable(httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION));
    }

    private CallCredentials getCredentials() {
        return getAuthorizationToken().isEmpty()
                ? new TenantMetadataProvider(getTenant())
                : new CompositeCallCredentials(
                        new TenantMetadataProvider(getTenant()),
                        new TokenForwardingProvider(getAuthorizationToken().get()));
    }

    public TenantId getTenantId() {
        return TenantId.newBuilder().setId(getTenant()).build();
    }
}

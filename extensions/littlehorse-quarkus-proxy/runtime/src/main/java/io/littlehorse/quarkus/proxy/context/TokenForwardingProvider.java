package io.littlehorse.quarkus.proxy.context;

import io.grpc.CallCredentials;
import io.grpc.Metadata;

import jakarta.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Executor;

public class TokenForwardingProvider extends CallCredentials {

    private static final Metadata.Key<String> AUTHORIZATION_HEADER =
            Metadata.Key.of(HttpHeaders.AUTHORIZATION, Metadata.ASCII_STRING_MARSHALLER);
    private final String authorizationToken;

    public TokenForwardingProvider(String authorizationToken) {
        if (StringUtils.isBlank(authorizationToken)) {
            throw new IllegalArgumentException("Authorization token was null or empty");
        }
        this.authorizationToken = authorizationToken;
    }

    @Override
    public void applyRequestMetadata(
            RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
        executor.execute(() -> {
            Metadata tenantMetadata = new Metadata();
            tenantMetadata.put(AUTHORIZATION_HEADER, authorizationToken);
            metadataApplier.apply(tenantMetadata);
        });
    }
}

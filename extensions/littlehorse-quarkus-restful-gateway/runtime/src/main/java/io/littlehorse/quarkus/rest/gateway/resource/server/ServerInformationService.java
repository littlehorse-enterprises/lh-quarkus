package io.littlehorse.quarkus.rest.gateway.resource.server;

import com.google.protobuf.Empty;

import io.littlehorse.quarkus.reactive.LittleHorseReactiveStub;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServerInformationService {
    private final LittleHorseReactiveStub stub;

    public ServerInformationService(LittleHorseReactiveStub stub) {
        this.stub = stub;
    }

    public Uni<ServerInformationResponse> get() {
        return stub.getServerVersion(Empty.getDefaultInstance())
                .map(ServerInformationResponse::new);
    }
}

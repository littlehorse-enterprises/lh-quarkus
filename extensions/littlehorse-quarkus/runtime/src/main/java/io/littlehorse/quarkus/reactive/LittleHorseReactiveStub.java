package io.littlehorse.quarkus.reactive;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.grpc.CallCredentials;
import io.grpc.CallOptions;
import io.grpc.ClientInterceptor;
import io.grpc.CompositeCallCredentials;
import io.grpc.Deadline;
import io.littlehorse.sdk.common.auth.TenantMetadataProvider;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseFutureStub;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Reactive wrapper around {@link LittleHorseFutureStub} that exposes every gRPC
 * call as a Mutiny {@link io.smallrye.mutiny.Uni}.
 *
 * <p>
 * The per-RPC methods are inherited from
 * {@link AbstractLittleHorseReactiveStub},
 * which is generated at build time by the {@code generateReactiveStub} Gradle
 * task
 * ({@code io.littlehorse.quarkus.gradle.GenerateReactiveStubTask}).
 *
 * <p>
 * This class holds the hand-maintained methods. Add new manual methods here.
 */
@ApplicationScoped
@Unremovable
@DefaultBean
public class LittleHorseReactiveStub extends AbstractLittleHorseReactiveStub {

    public LittleHorseReactiveStub(LittleHorseFutureStub futureStub) {
        super(futureStub);
    }

    /**
     * This method returns a LittleHorseReactiveStub pointing to a
     * specified tenant.
     * It Adds a TenantMetadataProvider to the end of the CallCredentials chain,
     * keeping
     * previous credentials configurations.
     * If you need to totally override the CallCredentials configurations
     * use withCallCredentials method.
     *
     * @param tenant Tenant ID.
     * @return a LittleHorseReactiveStub pointing to a new tenant.
     */
    public LittleHorseReactiveStub withTenant(String tenant) {
        CallCredentials newTenantCredentials = new TenantMetadataProvider(tenant);
        CallCredentials credentials = Optional.ofNullable(futureStub.getCallOptions())
                .map(CallOptions::getCredentials)
                .map(previousCredentials -> (CallCredentials) new CompositeCallCredentials(previousCredentials,
                        newTenantCredentials))
                .orElse(newTenantCredentials);
        return withCallCredentials(credentials);
    }

    public LittleHorseReactiveStub withWaitForReady() {
        return new LittleHorseReactiveStub(futureStub.withWaitForReady());
    }

    public <T> LittleHorseReactiveStub withWaitForReady(CallOptions.Key<T> key, T value) {
        return new LittleHorseReactiveStub(futureStub.withOption(key, value));
    }

    public LittleHorseReactiveStub withOnReadyThreshold(int numBytes) {
        return new LittleHorseReactiveStub(futureStub.withOnReadyThreshold(numBytes));
    }

    public LittleHorseReactiveStub withMaxOutboundMessageSize(int maxSize) {
        return new LittleHorseReactiveStub(futureStub.withMaxOutboundMessageSize(maxSize));
    }

    public LittleHorseReactiveStub withMaxInboundMessageSize(int maxSize) {
        return new LittleHorseReactiveStub(futureStub.withMaxInboundMessageSize(maxSize));
    }

    public LittleHorseReactiveStub withInterceptors(ClientInterceptor... interceptors) {
        return new LittleHorseReactiveStub(futureStub.withInterceptors(interceptors));
    }

    public LittleHorseReactiveStub withExecutor(Executor executor) {
        return new LittleHorseReactiveStub(futureStub.withExecutor(executor));
    }

    public LittleHorseReactiveStub withDeadlineAfter(Duration duration) {
        return new LittleHorseReactiveStub(futureStub.withDeadlineAfter(duration));
    }

    public LittleHorseReactiveStub withDeadlineAfter(long duration, TimeUnit unit) {
        return new LittleHorseReactiveStub(futureStub.withDeadlineAfter(duration, unit));
    }

    public LittleHorseReactiveStub withDeadline(Deadline deadline) {
        return new LittleHorseReactiveStub(futureStub.withDeadline(deadline));
    }

    public LittleHorseReactiveStub withCompression(String compressorName) {
        return new LittleHorseReactiveStub(futureStub.withCompression(compressorName));
    }

    public LittleHorseReactiveStub withCallCredentials(CallCredentials credentials) {
        return new LittleHorseReactiveStub(futureStub.withCallCredentials(credentials));
    }
}

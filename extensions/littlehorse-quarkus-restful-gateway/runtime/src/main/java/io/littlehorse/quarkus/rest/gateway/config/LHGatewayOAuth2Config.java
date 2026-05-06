package io.littlehorse.quarkus.rest.gateway.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * OAuth 2 configuration for the RESTful gateway.
 */
@ConfigMapping(prefix = "quarkus.littlehorse.gateway")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface LHGatewayOAuth2Config {

    /**
     * The OAuth 2 mode to use. Default is TOKEN_FORWARD.
     */
    @WithName("oauth2.mode")
    @WithDefault("TOKEN_FORWARD")
    OAuth2Mode oauth2Mode();

    /**
     * The role name that grants full access (read + write) to the gateway endpoints.
     * Only used when oauth2.mode is RBAC.
     */
    @WithName("oauth2.rbac.admin-role")
    @WithDefault("gateway-admin")
    String adminRole();

    /**
     * The role name that grants read-only access (GET endpoints) to the gateway endpoints.
     * Only used when oauth2.mode is RBAC.
     */
    @WithName("oauth2.rbac.reader-role")
    @WithDefault("gateway-reader")
    String readerRole();
}

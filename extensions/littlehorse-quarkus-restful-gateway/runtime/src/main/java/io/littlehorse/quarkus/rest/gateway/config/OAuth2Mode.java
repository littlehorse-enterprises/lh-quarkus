package io.littlehorse.quarkus.rest.gateway.config;

/**
 * OAuth 2 modes for the RESTful gateway.
 */
public enum OAuth2Mode {
    /**
     * None (disabled): no authentication is performed. No token is forwarded to the LH gRPC server
     * and no security annotations are enforced. Use this mode when the LH server is configured
     * without authentication (e.g., local development).
     */
    NONE,

    /**
     * Token forward (default): the HTTP request carries the Bearer token in the AUTHORIZATION header
     * and it is passed to LH gRPC server. This mode depends on LH for validating and authorizing the token.
     */
    TOKEN_FORWARD,

    /**
     * RBAC (Role-Based Access Control): the HTTP request carries the Bearer token in the AUTHORIZATION header
     * and it is validated at the gateway layer. It is not passed to LH gRPC server.
     * This mode depends on the gateway configurations and the roles configured on the IdP server.
     */
    RBAC
}

package io.littlehorse.quarkus.rest.gateway.security;

import io.littlehorse.quarkus.rest.gateway.config.LHGatewayOAuth2Config;
import io.littlehorse.quarkus.rest.gateway.config.OAuth2Mode;
import io.quarkus.security.identity.SecurityIdentity;

import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

/**
 * Enforces role-based access control only when {@link OAuth2Mode#RBAC} is active.
 * <p>
 * In {@code NONE} and {@code TOKEN_FORWARD} modes this filter is a no-op.
 * <p>
 * Policy:
 * <ul>
 *   <li>GET requests require either the admin or reader role.</li>
 *   <li>All other methods (POST, PUT, DELETE, …) require the admin role.</li>
 * </ul>
 */
@Provider
public class RbacRequestFilter implements ContainerRequestFilter {

    @Inject
    LHGatewayOAuth2Config config;

    @Inject
    SecurityIdentity identity;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (config.oauth2Mode() != OAuth2Mode.RBAC) {
            return;
        }

        String method = requestContext.getMethod();
        boolean isReadOnly = "GET".equalsIgnoreCase(method);

        if (isReadOnly) {
            if (!hasRole(config.adminRole()) && !hasRole(config.readerRole())) {
                throw new ForbiddenException("Insufficient permissions");
            }
        } else {
            if (!hasRole(config.adminRole())) {
                throw new ForbiddenException("Insufficient permissions");
            }
        }
    }

    private boolean hasRole(String role) {
        return identity.hasRole(role);
    }
}

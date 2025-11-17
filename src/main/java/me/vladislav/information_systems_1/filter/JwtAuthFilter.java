package me.vladislav.information_systems_1.filter;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import me.vladislav.information_systems_1.utils.JwtUtil;

import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (path.startsWith("/auth") || path.startsWith("/events")) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                    jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.UNAUTHORIZED)
                            .entity("Authorization header must be provided")
                            .build()
            );
            return;
        }

        String token = authHeader.substring("Bearer ".length());
        Claims claims;
        String login;
        String role;
        try {
            claims = JwtUtil.validateToken(token);
            login = claims.getSubject();
            role = claims.get("role", String.class);
        } catch (Exception e) {
            requestContext.abortWith(
                    jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.UNAUTHORIZED)
                            .entity("Invalid or expired token")
                            .build()
            );
            return;
        }

        final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return () -> login;
            }

            @Override
            public boolean isUserInRole(String r) {
                return role != null && role.equalsIgnoreCase(r);
            }

            @Override
            public boolean isSecure() {
                return currentSecurityContext.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "Bearer";
            }
        });
    }
}

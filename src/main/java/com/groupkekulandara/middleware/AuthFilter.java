package com.groupkekulandara.middleware;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import java.io.IOException;

@Provider
public class AuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // 1. Only protect paths starting with "admin"
        if (path.contains("admin")) {

            // 2. Check for a Custom Header or Session Cookie
            // For a simple uni project, check if a 'User-Role' header exists
            String authHeader = requestContext.getHeaderString("User-Role");

            if (authHeader == null || !authHeader.equals("ADMIN")) {
                // 3. Stop the request and return 401 Unauthorized
                requestContext.abortWith(Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Admin access required\"}")
                        .build());
            }
        }
    }
}
package com.example.restdemo.web.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Collections;

@Provider
public class PatchingHeaderFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        if ("OPTIONS".equals(requestContext.getMethod()) &&
                responseContext.getHeaderString("Accept-Patch") == null) {
            responseContext.getHeaders().put(
                    "Accept-Patch", Collections.singletonList("application/json-patch+json"));
        }
    }
}

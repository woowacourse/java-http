package org.apache.coyote.http11.filter;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class DefaultFilter implements Filter{
    @Override
    public Response doFilter(Request request, FilterChain filterChain) {
        return new Response();
    }
}

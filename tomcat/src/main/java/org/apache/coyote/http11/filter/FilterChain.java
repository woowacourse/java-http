package org.apache.coyote.http11.filter;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public interface FilterChain {
    Response doFilter(Request request);
}

package org.apache.coyote.http11.filter;

import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;

public interface Filter {
    Response doFilter(Request request, FilterChain chain);
}

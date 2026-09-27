package org.apache.catalina.filter;

import org.apache.coyote.http11.data.HttpRequest;

public interface Filter {
    void doFilter(HttpRequest request, FilterChain chain);
}

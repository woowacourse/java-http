package org.apache.coyote.http11.filter;

import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;

public interface Filter {
    HttpResponse doFilter(HttpRequest request, FilterChain chain);
}

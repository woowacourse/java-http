package org.apache.coyote.http11.filter;

import java.util.function.BiConsumer;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;

public class FilterChain {

    private final Filter[] filters;
    private final BiConsumer<HttpRequest, HttpResponse> target;

    private int currentFilterIndex = 0;

    FilterChain(
            final BiConsumer<HttpRequest, HttpResponse> target,
            final Filter... filters) {
        this.target = target;
        this.filters = filters;
    }

    public void doFilter(HttpRequest request, HttpResponse response) {
        if (currentFilterIndex < filters.length) {
            filters[currentFilterIndex++].doFilter(request, this);
        }

        target.accept(request, response);
    }
}

package org.apache.catalina.filter;

import java.util.function.BiConsumer;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;

public class FilterChainFactory {

    private final Filter[] filters;

    public FilterChainFactory(Filter... filters) {
        this.filters = filters.clone();
    }

    public FilterChain create(BiConsumer<HttpRequest, HttpResponse> target) {
        return new FilterChain(target, filters);
    }
}

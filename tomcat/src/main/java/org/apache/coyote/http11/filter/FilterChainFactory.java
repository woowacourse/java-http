package org.apache.coyote.http11.filter;

import java.util.function.Function;
import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;

public class FilterChainFactory {

    private final Filter[] filters;

    public FilterChainFactory(Filter... filters) {
        this.filters = filters.clone();
    }

    public FilterChain create(Function<Request, Response> target) {
        return new FilterChain(target, filters);
    }
}

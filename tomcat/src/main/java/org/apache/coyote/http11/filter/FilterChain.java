package org.apache.coyote.http11.filter;

import java.util.function.Function;
import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;

public class FilterChain {

    private final Filter[] filters;
    private final Function<Request, Response> target;

    private int currentFilterIndex = 0;

    FilterChain(
            final Function<Request, Response> target,
            final Filter... filters) {
        this.target = target;
        this.filters = filters;
    }

    public Response doFilter(Request request) {
        if (currentFilterIndex < filters.length) {
            Filter currentFilter = filters[currentFilterIndex++];
            return currentFilter.doFilter(request, this);
        }

        return target.apply(request);
    }
}

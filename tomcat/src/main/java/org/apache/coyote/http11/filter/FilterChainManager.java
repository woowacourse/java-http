package org.apache.coyote.http11.filter;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class FilterChainManager {

    private final Chain defaultChain;
    private Chain initialChain;
    private Chain lastChain;

    public FilterChainManager() {
        this.defaultChain = new Chain(new DefaultFilter());
    }

    public void add(Filter filter) {
        Chain chain = new Chain(filter);
        if (lastChain == null) {
            initialChain = chain;
            lastChain = chain;
            initialChain.next = lastChain;
            lastChain.next = defaultChain;
            return;
        }
        lastChain.next = chain;
        lastChain = chain;
        lastChain.next = defaultChain;
    }

    public FilterChain getInitialChain() {
        return initialChain;
    }

    private class Chain implements FilterChain {
        private final Filter filter;
        private Chain next;

        public Chain(Filter filter) {
            this.filter = filter;
        }

        @Override
        public void doFilter(Request request, Response response) {
            filter.doFilter(request, response, next);
        }
    }
}

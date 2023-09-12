package org.apache.coyote.http11.filter;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class FilterChainManager {

    private final Chain defaultChain;
    private Chain initialChain;
    private Chain lastChain;

    public FilterChainManager() {
        this.defaultChain = new Chain(new DefaultFilter());
        initialChain = lastChain = defaultChain;
    }

    public void add(Filter filter) {
        Chain chain = new Chain(filter);
        if(initialChain.equals(defaultChain)){
            initialChain = chain;
            initialChain.next = defaultChain;
            return;
        }
        if(lastChain.equals(defaultChain)){
            initialChain.next = chain;
            lastChain = chain;
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
            if(!response.isFiltered()){
                filter.doFilter(request, response, next);
            }
        }
    }
}

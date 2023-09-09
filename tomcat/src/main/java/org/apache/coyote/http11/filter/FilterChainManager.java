package org.apache.coyote.http11.filter;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class FilterChainManager {

    private Chain initialChain;
    private Chain lastChain;
    private final Chain defaultChain;

    public FilterChainManager() {
        this.defaultChain = new Chain(new DefaultFilter());
    }

    public void add(Filter filter){
        Chain chain = new Chain(filter);
        if(lastChain == null){
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

    private class Chain implements FilterChain{
        private Chain next;
        private final Filter filter;

        public Chain(Filter filter) {
            this.filter = filter;
        }

        @Override
        public Response doFilter(Request request) {
            return filter.doFilter(request,next);
        }
    }
}

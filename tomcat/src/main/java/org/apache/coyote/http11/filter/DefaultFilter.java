package org.apache.coyote.http11.filter;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public void doFilter(Request request, Response response, FilterChain filterChain) {
        log.info("Non filtered by filter chain");
    }
}

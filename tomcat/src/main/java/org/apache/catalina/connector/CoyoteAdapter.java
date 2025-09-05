package org.apache.catalina.connector;

import org.apache.catalina.mapper.RequestMapper;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoyoteAdapter implements Adapter {

    private static final Logger log = LoggerFactory.getLogger(CoyoteAdapter.class);

    private final RequestMapper requestMapper;
    private final StaticResourceHandler staticResourceHandler;

    public CoyoteAdapter() {
        this.requestMapper = new RequestMapper();
        this.staticResourceHandler = new StaticResourceHandler();
    }

    @Override
    public void service(final Http11Request request, final Http11Response response) {
        final var controller = requestMapper.findController(request);
        controller.service(request, response);
        try {
            staticResourceHandler.handleStaticResource(response);
        } catch (Exception e) {
            log.error("Static resource handling failed", e);
            response.setStatusCode(500);
            response.setResourcePath("/500.html");
        }
    }
}

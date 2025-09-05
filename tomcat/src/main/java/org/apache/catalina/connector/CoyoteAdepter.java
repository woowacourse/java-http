package org.apache.catalina.connector;

import org.apache.catalina.mapper.RequestMapper;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class CoyoteAdepter implements Adapter {

    private final RequestMapper requestMapper;

    public CoyoteAdepter() {
        this.requestMapper = new RequestMapper();
    }

    @Override
    public void service(Http11Request request, Http11Response response) {
        try {
            final var controller = requestMapper.findController(request);
            controller.service(request, response);
        } catch (Exception ignored) {
        }
    }
}

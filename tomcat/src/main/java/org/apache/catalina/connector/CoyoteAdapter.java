package org.apache.catalina.connector;

import org.apache.catalina.mapper.RequestMapper;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class CoyoteAdapter implements Adapter {

    private final RequestMapper requestMapper;

    public CoyoteAdapter() {
        this.requestMapper = new RequestMapper();
    }

    @Override
    public void service(Http11Request request, Http11Response response) {
        final var controller = requestMapper.findController(request);
        controller.service(request, response);
    }
}

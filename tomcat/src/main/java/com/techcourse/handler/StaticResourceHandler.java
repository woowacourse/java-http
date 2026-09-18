package com.techcourse.handler;

import java.io.IOException;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;
import org.apache.coyote.http.StaticResourceBody;

public class StaticResourceHandler implements ResourceHandler {

    @Override
    public boolean canHandle(HttpServletRequest request) {
        return true;
    }

    @Override
    public HttpServletResponse handle(HttpServletRequest request) throws IOException {
        return HttpServletResponse.ok(StaticResourceBody.from(request.path()));
    }
}

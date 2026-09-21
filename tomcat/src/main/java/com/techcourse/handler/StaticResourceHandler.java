package com.techcourse.handler;

import java.io.IOException;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.StaticResourceBody;

public class StaticResourceHandler implements ResourceHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        return HttpResponse.ok(StaticResourceBody.from(request.path()));
    }
}

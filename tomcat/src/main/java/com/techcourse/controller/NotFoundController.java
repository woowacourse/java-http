package com.techcourse.controller;

import org.apache.coyote.http.HttpBody;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.HttpStatusLine;

public class NotFoundController implements Controller {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        HttpStatusLine statusLine = new HttpStatusLine(request.getHttpVersion(), HttpStatusCode.NOT_FOUND);
        String resource = StaticResourceHandler.handle("/404.html");
        HttpBody responseBody = new HttpBody(resource);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .body(responseBody)
                .build();
    }
}

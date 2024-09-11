package com.techcourse.controller;

import static org.apache.coyote.http.MediaType.TEXT_HTML;

import org.apache.coyote.http.HttpBody;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.HttpStatusLine;

public class RootController implements Controller {


    @Override
    public HttpResponse handle(final HttpRequest request) {
        var status = HttpStatusCode.OK;
        var statusLine = new HttpStatusLine(request.getHttpVersion(), status);
        var body = new HttpBody("Hello world!");
        return HttpResponse.builder()
                .statusLine(statusLine)
                .contentType(TEXT_HTML.defaultCharset())
                .body(body)
                .build();
    }
}

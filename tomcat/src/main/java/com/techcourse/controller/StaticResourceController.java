package com.techcourse.controller;

import static org.apache.coyote.http.MediaType.APPLICATION_JAVASCRIPT;
import static org.apache.coyote.http.MediaType.TEXT_CSS;
import static org.apache.coyote.http.MediaType.TEXT_HTML;

import org.apache.coyote.http.HttpBody;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.HttpStatusLine;
import org.apache.coyote.http.StaticResourceHandler;

public class StaticResourceController implements Controller {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        HttpStatusCode statusCode = HttpStatusCode.OK;
        HttpStatusLine statusLine = new HttpStatusLine(request.getHttpVersion(), statusCode);
        String content = StaticResourceHandler.handle(request.getPath());
        HttpBody body = new HttpBody(content);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .contentType(contentType(request))
                .body(body)
                .build();
    }

    private String contentType(final HttpRequest request) {
        String path = request.getPath();
        String accept = request.getAccept();
        if (path.endsWith(".html") || accept.contains(TEXT_HTML.value())) {
            return TEXT_HTML.defaultCharset();
        }
        if (path.endsWith(".css") || accept.contains(TEXT_CSS.value())) {
            return TEXT_CSS.defaultCharset();
        }
        if (path.endsWith(".js") || accept.contains(APPLICATION_JAVASCRIPT.value())) {
            return APPLICATION_JAVASCRIPT.defaultCharset();
        }
        return null;
    }
}

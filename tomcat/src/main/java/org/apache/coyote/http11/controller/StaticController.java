package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.ViewLoader;

public class StaticController implements Controller {

    private static final String INDEX_URI = "/";

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (request.getUri().equals(INDEX_URI)) {
            return HttpResponse.builder()
                    .statusCode(StatusCode.OK)
                    .contentType(ContentType.TEXT_HTML)
                    .responseBody("Hello world!")
                    .build();
        }

        return HttpResponse.builder()
                .statusCode(StatusCode.OK)
                .contentType(ContentType.from(request.getExtension()))
                .responseBody(ViewLoader.from(request.getUri()))
                .build();
    }
}

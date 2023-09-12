package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.ViewLoader;

public class StaticController extends AbstractController {

    private static final String INDEX_URI = "/";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getPath().equals(INDEX_URI)) {
            response
                .statusCode(StatusCode.OK)
                .contentType(ContentType.TEXT_HTML)
                .responseBody("Hello world!");
            return;
        }

        response
            .statusCode(StatusCode.OK)
            .contentType(ContentType.from(request.getExtension()))
            .responseBody(ViewLoader.from(request.getPath()));
    }
}

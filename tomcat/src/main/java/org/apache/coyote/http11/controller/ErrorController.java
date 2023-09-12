package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.ViewLoader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ErrorController extends AbstractController {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        response
            .statusCode(StatusCode.NOT_FOUND)
            .contentType(ContentType.TEXT_HTML)
            .responseBody(ViewLoader.toNotFound());
    }
}

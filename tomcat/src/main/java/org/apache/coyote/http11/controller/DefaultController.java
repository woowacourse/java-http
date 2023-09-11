package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

import static org.apache.coyote.http11.ViewResolver.resolveView;
import static org.apache.coyote.http11.types.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.types.HttpStatus.OK;

public class DefaultController extends AbstractController {

    private static final String DEFAULT_MESSAGE = "Hello world!";
    private static final String DEFAULT_URI = "/";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (DEFAULT_URI.equals(request.getPath())) {
            response.setBody(DEFAULT_MESSAGE, TEXT_HTML);
            response.setHttpStatus(OK);
            return;
        }

        resolveView(request, response);
    }
}

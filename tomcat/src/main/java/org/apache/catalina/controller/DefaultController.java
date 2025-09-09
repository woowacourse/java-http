package org.apache.catalina.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class DefaultController extends AbstractController {

    public static final String DEFAULT_BODY = "Hello world!";

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return httpRequest.isDefaultRequestPath();
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.init(DEFAULT_BODY, ContentType.PLAIN, HttpStatus.OK);
    }
}

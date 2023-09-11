package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;

public class RootController extends AbstractController {
    private static final Uri ROOT_URI = Uri.ROOT;

    @Override
    public boolean canHandle(final HttpRequest request) {
        final String path = request.getRequestLine().getPath();
        return path.equals(ROOT_URI.getFullPath());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        final ResponseBody responseBody = ResponseBody.rootContent();
        response.setHttpStatus(HttpStatus.OK);
        response.setResponseBody(responseBody);
        response.setResponseHeaders(responseBody);
    }
}

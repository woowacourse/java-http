package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

public class UnAuthorizedController extends AbstractController {
    private static final Uri UNAUTHORIZED_URI = Uri.UNAUTHORIZED;

    @Override
    public boolean canHandle(final HttpRequest request) {
        final String path = request.getRequestLine().getPath();
        return path.startsWith(UNAUTHORIZED_URI.getSimplePath());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final StaticResource staticResource = StaticResource.from(UNAUTHORIZED_URI.getFullPath());
        final ResponseBody responseBody = ResponseBody.from(staticResource);
        response.setHttpStatus(HttpStatus.OK);
        response.setResponseBody(responseBody);
        response.setResponseHeaders(responseBody);
    }
}

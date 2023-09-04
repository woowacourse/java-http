package org.apache.coyote.httpresponse.handler;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httprequest.RequestMethod;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class RegisterHandler implements Handler {
    @Override
    public HttpResponse handle(final HttpRequest request) {
        final RequestMethod requestMethod = request.getRequestMethod();
        if (requestMethod == RequestMethod.POST) {
            return handlePost(request);
        }
        if (requestMethod == RequestMethod.GET) {
            return handleGet(request);
        }
        return null;
    }

    private HttpResponse handlePost(final HttpRequest request) {

    }

    private HttpResponse handleGet(final HttpRequest request) {
        final HttpResponse initialResponse = HttpResponse.init(request.getHttpVersion());
        final HttpResponse afterSetHttpStatus = initialResponse.setHttpStatus(HttpStatus.OK);
        final String resourcePath = request.getPath() + ".html";
        final HttpResponse afterSetContent = afterSetHttpStatus.setContent(resourcePath, request.getQueryString());
        return afterSetContent;
    }
}

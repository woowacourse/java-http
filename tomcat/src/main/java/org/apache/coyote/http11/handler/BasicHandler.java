package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class BasicHandler {

    private static final String ROOT_RESOURCE = "Hello world!";

    private BasicHandler() {
    }

    public static HttpResponse handle(final HttpRequest httpRequest) {
        return HttpResponse.okResponse(httpRequest, ROOT_RESOURCE);
    }
}

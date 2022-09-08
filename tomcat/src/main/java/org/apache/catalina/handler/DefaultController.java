package org.apache.catalina.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return null;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        throw new IllegalArgumentException("Unsupported Method for this path: \"/\"");
    }
}

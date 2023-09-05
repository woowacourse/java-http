package org.apache.coyote.http11.handler.controller.base;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws Exception {
        if (httpRequest.isGetMethod()) {
            return doGet(httpRequest);
        }

        if (httpRequest.isPostMethod()) {
            return doPost(httpRequest);
        }

        throw new IllegalArgumentException("Abstract 예외 발생");
    }

    protected HttpResponse doGet(HttpRequest httpRequest) throws Exception {
        return null;
    }

    protected HttpResponse doPost(HttpRequest httpRequest) throws Exception {
        return null;
    }
}

package org.apache.coyote.http11.handler;

import java.util.HashMap;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponseHeader;

public abstract class HttpRequestHandler implements RequestHandler {

    protected String EMPTY_BODY = "";

    @Override
    public HandlerResponseEntity handle(final HttpRequest httpRequest) {
        final HttpResponseHeader responseHeader = new HttpResponseHeader(new HashMap<>());
        if (httpRequest.isGet()) {
            return doGet(httpRequest, responseHeader);
        }
        return doPost(httpRequest, responseHeader);
    }

    protected abstract HandlerResponseEntity doGet(HttpRequest httpRequest, HttpResponseHeader responseHeader);

    protected abstract HandlerResponseEntity doPost(HttpRequest httpRequest, HttpResponseHeader responseHeader);
}

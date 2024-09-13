package org.apache.catalina.controller;

import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestLine;
import org.apache.coyote.http11.message.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private final String baseUri;

    public AbstractController(final String baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public boolean canHandleRequest(final HttpRequest request) {
        return request.getRequestLine().matchRequestUri(baseUri);
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        final HttpRequestLine requestLine = request.getRequestLine();
        if (requestLine.matchHttpMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }

        if (requestLine.matchHttpMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }

        // TODO : Method Not Supported 구현
    }

    protected abstract void doGet(final HttpRequest request, final HttpResponse response);

    protected abstract void doPost(final HttpRequest request, final HttpResponse response);
}

package org.apache.catalina.controller;

import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestLine;
import org.apache.coyote.http11.message.request.HttpRequestUri;
import org.apache.coyote.http11.message.response.HttpResponse;

public abstract class AbstractController implements Controller {

    protected boolean matchRequestUriWithBaseUri(final HttpRequest httpRequest, final String baseUri) {
        final HttpRequestLine requestLine = httpRequest.getRequestLine();
        final HttpRequestUri requestUri = requestLine.getHttpRequestUri();
        return requestUri.matchRequestUri(baseUri);
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
        }
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response);

    protected abstract void doPost(HttpRequest request, HttpResponse response);
}

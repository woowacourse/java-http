package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        if (httpMethod.equals(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (httpMethod.equals(HttpMethod.POST)) {
            doPost(httpRequest, httpResponse);
            return;
        }
        httpResponse.methodNotAllowed();
    }

    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.methodNotAllowed();
    }

    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.methodNotAllowed();
    }
}

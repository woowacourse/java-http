package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest httpRequest) {
        if (httpRequest.isMethod("GET")) {
            return doGet(httpRequest);
        } else if (httpRequest.isMethod("POST")) {
            return doPost(httpRequest);
        }

        throw new RuntimeException();
    }

    abstract protected HttpResponse doPost(HttpRequest httpRequest);

    abstract protected HttpResponse doGet(HttpRequest httpRequest);
}

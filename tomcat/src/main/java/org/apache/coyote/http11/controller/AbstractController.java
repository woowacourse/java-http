package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest httpRequest) {
        if (httpRequest.isMethod("GET")) {
            return serviceGet(httpRequest);
        } else if (httpRequest.isMethod("POST")) {
            return servicePost(httpRequest);
        }

        throw new RuntimeException();
    }

    abstract protected HttpResponse servicePost(HttpRequest httpRequest);

    abstract protected HttpResponse serviceGet(HttpRequest httpRequest);
}

package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest httpRequest) {
        if (httpRequest.isMethod(HttpMethod.GET)) {
            return doGet(httpRequest);
        }
        if (httpRequest.isMethod(HttpMethod.POST)) {
            return doPost(httpRequest);
        }

        throw new IllegalArgumentException("유효하지 않은 메소드입니다.");
    }

    abstract protected HttpResponse doPost(HttpRequest httpRequest);

    abstract protected HttpResponse doGet(HttpRequest httpRequest);
}

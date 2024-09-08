package com.techcourse.controller.model;

import org.apache.coyote.http11.request.domain.RequestLine;
import org.apache.coyote.http11.request.domain.RequestMethod;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.response.model.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        if (requestLine.isSameMethod(RequestMethod.GET)) {
            return doGet(httpRequest);
        }
        if (requestLine.isSameMethod(RequestMethod.POST)) {
            return doPost(httpRequest);
        }
        throw new IllegalArgumentException("지원하지 않는 매서드 입니다.");
    }
}

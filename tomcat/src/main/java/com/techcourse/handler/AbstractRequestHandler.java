package com.techcourse.handler;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.http.HttpMethod;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.response.HttpResponse;

public abstract class AbstractRequestHandler implements RequestHandler {

    @Override
    public HttpResponse service(HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getHttpMethod();

        if (httpMethod == HttpMethod.GET) {
            return doGet(httpRequest);
        }
        if (httpMethod == HttpMethod.POST) {
            return doPost(httpRequest);
        }

        throw new UncheckedServletException("지원하는 Http Method가 아닙니다.");
    }

    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new UncheckedServletException("지원하지 않는 Http Method 입니다.");
    }

    protected HttpResponse doGet(HttpRequest httpRequest) {
        throw new UncheckedServletException("지원하지 않는 Http Method 입니다.");
    }
}


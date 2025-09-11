package com.techcourse.controller.core;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class AbstractRequestController implements RequestController {

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


package com.techcourse.controller;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
            return;
        }
        throw new UncheckedServletException("지원하지 않는 메서드입니다.");
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;
}

package com.techcourse.controller;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String SUPPORT_POST_CONTENT_TYPE = "application/x-www-form-urlencoded";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (HttpMethod.GET == request.getMethod()) {
            doGet(request, response);
            return;
        }
        if (HttpMethod.POST == request.getMethod() && SUPPORT_POST_CONTENT_TYPE.equals(request.getContentType())) {
            doPost(request, response);
            return;
        }
        throw new UncheckedServletException("지원하지 않는 메서드입니다.");
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;
}

package com.techcourse.controller;

import com.techcourse.controller.model.AbstractController;
import org.apache.coyote.http11.domain.body.ContentType;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.response.domain.HttpStatus;
import org.apache.coyote.http11.response.maker.HttpResponseMaker;
import org.apache.coyote.http11.response.model.HttpResponse;

public class DefaultController extends AbstractController {

    public static final String DEFAULT_BODY = "Hello world!";

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.isDefaultRequestPath();
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {
        throw new IllegalArgumentException("해당되는 메서드의 요청을 찾지 못했습니다.");
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        return HttpResponseMaker.make(DEFAULT_BODY, ContentType.PLAIN, HttpStatus.OK);
    }
}

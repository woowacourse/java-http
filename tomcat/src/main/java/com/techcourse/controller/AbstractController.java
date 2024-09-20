package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    protected static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isGetMethod()) {
            doGet(request, response);
            return;
        }
        if (request.isPostMethod()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedOperationException("지원하지 않는 GET 메서드 요청입니다.");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedOperationException("지원하지 않는 POST 메서드 요청입니다.");
    }
}

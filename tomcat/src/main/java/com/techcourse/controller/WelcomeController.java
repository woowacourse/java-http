package com.techcourse.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class WelcomeController implements Controller {

    @Override
    public void service(HttpRequest req, HttpResponse resp) {
        if (req.getMethod().equals(HttpMethod.GET)) {
            resp.setResponse("200 OK", "Hello world!");
        } else {
            throw new IllegalArgumentException("%s 요청을 처리할 컨트롤러가 존재하지 않습니다.".formatted(req.getMethod().getValue()));
        }
    }
}

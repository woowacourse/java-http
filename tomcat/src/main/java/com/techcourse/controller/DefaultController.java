package com.techcourse.controller;

import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);

    @Override
    public void service(Http11Request request, Http11Response response) {
        log.warn("Path:{} 페이지를 찾을 수 없습니다", request.path());
        response.setStatusCode(404);
        response.setResourcePath("/404.html");
    }

    @Override
    public void toGet(Http11Request request, Http11Response response) {
    }

    @Override
    public void toPost(Http11Request request, Http11Response response) {
    }
}

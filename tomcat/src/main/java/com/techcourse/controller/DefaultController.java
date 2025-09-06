package com.techcourse.controller;

import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Todo: 클래스 네이밍 변경 고려 [2025-09-05 17:19:32]
public class DefaultController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);

    @Override
    public void service(Http11Request request, Http11Response response) {
        log.info("Path:{}", request.path());
    }

    @Override
    public void toGet(Http11Request request, Http11Response response) {
    }

    @Override
    public void toPost(Http11Request request, Http11Response response) {
    }
}

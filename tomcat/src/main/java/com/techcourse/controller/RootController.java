package com.techcourse.controller;

import static org.apache.coyote.HttpStatus.OK;

import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.request.ServletRequest;
import org.apache.catalina.response.ServletResponse;
import org.apache.coyote.HttpHeaderName;

public class RootController extends AbstractController {

    @Override
    protected void doGet(ServletRequest request, ServletResponse response) {
        response.setStatus(OK);
        response.setHeader(HttpHeaderName.CONTENT_TYPE.getValue(), "text/html;charset=utf-8");
        response.setBody("Hello world!");
    }

    @Override
    protected void doPost(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("POST 요청은 지원하지 않습니다.");
    }
}

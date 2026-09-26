package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.request.Method;
import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;
import org.apache.coyote.response.StatusCode;

import java.util.List;

public class RootController extends AbstractController {

    @Override
    protected List<Method> allowedMethods() {
        return List.of(Method.GET);
    }

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws Exception {
        response.setStatusCode(StatusCode.OK);
        response.setContentType(request.getContentType());
        response.writeBody("Hello world!");
    }
}

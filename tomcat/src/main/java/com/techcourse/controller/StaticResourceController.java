package com.techcourse.controller;

import com.techcourse.StaticResourceReader;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;
import org.apache.coyote.response.StatusCode;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws Exception {
        response.setStatusCode(StatusCode.OK);
        response.setContentType(request.getContentType());
        final var responseBody = StaticResourceReader.read(request.getResourcePath());
        response.writeBody(responseBody);
    }
}

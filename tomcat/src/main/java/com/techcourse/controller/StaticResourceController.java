package com.techcourse.controller;

import com.techcourse.StaticResourceReader;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;
import org.apache.coyote.response.StatusCode;

import java.util.Optional;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws Exception {
        Optional<String> responseBody = StaticResourceReader.read(request.getResourcePath());
        if (responseBody.isEmpty()) {
            response.setStatusCode(StatusCode.NOT_FOUND);
            response.setContentType(ContentType.HTML);
            response.writeBody(StaticResourceReader.read("static/404.html").orElse("requested resource not found."));
            return;
        }

        response.setStatusCode(StatusCode.OK);
        response.setContentType(request.getContentType());
        response.writeBody(responseBody.get());
    }
}

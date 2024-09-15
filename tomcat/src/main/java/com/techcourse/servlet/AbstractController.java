package com.techcourse.servlet;

import java.io.IOException;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isSameMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.isSameMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }

        responseMethodNotAllowedPage(response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        responseMethodNotAllowedPage(response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        responseMethodNotAllowedPage(response);
    }

    private void responseMethodNotAllowedPage(HttpResponse response) throws IOException {
        response.setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.setStaticResourceResponse("/405.html");
        response.write();
    }
}

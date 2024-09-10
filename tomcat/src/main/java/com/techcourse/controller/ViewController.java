package com.techcourse.controller;

import java.io.IOException;
import com.techcourse.view.View;
import com.techcourse.view.ViewResolver;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ViewController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        responseNotFoundPage(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getRequestLine().getPath();

        if (path.equals("/")) {
            responseDefaultPage(request, response);
        } else {
            View view = ViewResolver.getView(path);
            if (view == null) {
                responseNotFoundPage(request, response);
            } else {
                responseResource(request, response, view);
            }
        }
    }

    private void responseDefaultPage(HttpRequest request, HttpResponse response) {
        response.setStatus200();
        response.setContentType(request.getContentType());
        response.setResponseBody("Hello world!");
    }

    private void responseNotFoundPage(HttpRequest request, HttpResponse response) throws IOException {
        View notFoundView = ViewResolver.getView("/404.html");
        response.setStatus404();
        response.setResponseBody(notFoundView.getContent());
        response.setContentType(request.getContentType());
    }

    private void responseResource(HttpRequest request, HttpResponse response, View view) {
        response.setStatus200();
        response.setResponseBody(view.getContent());
        response.setContentType(request.getContentType());
    }
}

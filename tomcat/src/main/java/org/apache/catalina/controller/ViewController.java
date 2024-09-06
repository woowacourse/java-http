package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.catalina.view.View;
import org.apache.catalina.view.ViewResolver;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ViewController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getRequestLine().getPath();

        if (path.equals("/")) {
            responseDefaultPage(response);
        } else {
            View view = ViewResolver.getView(path);
            if (view == null) {
                responseNotFoundPage(response);
            } else {
                responseResource(request, response, view);
            }
        }
    }

    private void responseDefaultPage(HttpResponse response) {
        response.setStatus200();
        response.setContentTypeHtml();
        response.setResponseBody("Hello world!");
    }

    private void responseNotFoundPage(HttpResponse response) throws IOException {
        View notFoundView = ViewResolver.getView("/404.html");
        response.setStatus404();
        response.setResponseBody(notFoundView.getContent());
        response.setContentTypeHtml();
    }

    private static void responseResource(HttpRequest request, HttpResponse response, View view) {
        response.setStatus200();
        response.setResponseBody(view.getContent());

        String accept = request.getHeaders().get("Accept");
        if (accept != null && accept.contains("text/css")) {
            response.setContentTypeCss();
        } else {
            response.setContentTypeHtml();
        }
    }
}

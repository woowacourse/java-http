package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String path = request.getPath();
        final byte[] fileBytes = request.readAllBytes(path);

        response.addHeader("Content-Type", getContentType(path) + ";charset=utf-8");
        response.setBody(new String(fileBytes));
        response.send();
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        return "text/html";
    }
}

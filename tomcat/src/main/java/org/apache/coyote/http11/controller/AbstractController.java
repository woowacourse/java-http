package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.file.RequestFactory;
import org.apache.coyote.http11.file.ResponseFactory;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class AbstractController implements Controller {
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String method = request.getMethod();
        if (method.equals(POST_METHOD)) {
            doPost(request, response);
            return;
        }
        if (method.equals(GET_METHOD)) {
            doGet(request, response);
            return;
        }

        throw new IllegalArgumentException("지원하지 않는 Http Method 입니다.");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusLine("302", "Found");
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusLine("200", "OK");

        String responseBody = ResponseFactory.getResponseBody(request);
        response.setBody(responseBody);
        response.setFieldValue("Content-Length", String.valueOf(responseBody.length()));
        response.setFieldValue("Content-Type", request.getMimeType());
    }
}

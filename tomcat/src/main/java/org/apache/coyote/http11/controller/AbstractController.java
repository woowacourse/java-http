package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class AbstractController implements Controller {
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";
    private static final String STATIC_PATH = "static";
    private static final String CHARSET_UTF8 = ";charset=UTF-8";

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

        String responseBody = getResponseBody(request);
        response.setBody(responseBody);
        response.setFieldValue("Content-Length", String.valueOf(responseBody.length()));
        response.setFieldValue("Content-Type", request.getMimeType() + CHARSET_UTF8);
    }

    private String getResponseBody(HttpRequest request) {
        try {
            String requestUri = request.getRequestUri();
            URL resourceUrl = getClass().getClassLoader().getResource(STATIC_PATH + requestUri);
            if (Objects.isNull(resourceUrl)) {
                return "";
            }

            File file = new File(resourceUrl.getFile());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽을 수 없습니다.");
        }
    }
}

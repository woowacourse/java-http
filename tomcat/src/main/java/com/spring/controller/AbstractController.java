package com.spring.controller;

import com.spring.http.enums.HttpMethod;
import com.spring.http.enums.HttpStatus;
import com.spring.http.request.HttpRequest;
import com.spring.http.response.HttpResponse;
import java.io.IOException;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        final HttpMethod method = request.requestStartLine().method();

        switch (method) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> response.sendError(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메서드입니다.");
        }
    }
}

package com.spring.controller;

import com.spring.http.enums.HttpStatus;
import com.spring.http.request.HttpRequest;
import com.spring.http.response.HttpResponse;
import java.io.IOException;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws IOException;

    default void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메서드입니다.");
    }

    default void doPost(HttpRequest request, HttpResponse response) throws IOException {
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메서드입니다.");
    }
}

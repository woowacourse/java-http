package com.techcourse.controller;

import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setHeader("Content-Type", List.of("text/html;charset=utf-8 "));
        response.setBody("Hello world!".getBytes(StandardCharsets.UTF_8));
    }
}

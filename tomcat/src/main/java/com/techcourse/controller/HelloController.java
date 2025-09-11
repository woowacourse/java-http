package com.techcourse.controller;

import com.techcourse.ResponseWriters;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HelloController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        ResponseWriters.ok(response, "Hello world!".getBytes(StandardCharsets.UTF_8));
    }
}

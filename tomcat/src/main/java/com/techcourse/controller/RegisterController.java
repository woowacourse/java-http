package com.techcourse.controller;

import com.techcourse.ResponseWriters;
import com.techcourse.service.Service;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final Service service;

    public RegisterController(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        ResponseWriters.ok(response, "register.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> signInRequest = request.parseForBody();
        service.create(signInRequest);
        ResponseWriters.found(response, "/index.html");
    }
}

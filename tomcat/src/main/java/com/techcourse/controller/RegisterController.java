package com.techcourse.controller;

import com.techcourse.controller.dto.RegisterRequest;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.util.StaticResourceManager;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String STATIC_RESOURCE_PATH = "/register.html";

    @Override
    public String getPath() {
        return "/register";
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        ResponseFile file = StaticResourceManager.read(STATIC_RESOURCE_PATH);
        response.setStatus(HttpStatusCode.OK)
                .setBody(file);
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse response) {
        RegisterRequest registerRequest = RegisterRequest.of(httpRequest.getBody());
        User user = new User(registerRequest.account(), registerRequest.password(), registerRequest.email());
        InMemoryUserRepository.save(user);

        response.sendRedirect("index.html");
    }
}

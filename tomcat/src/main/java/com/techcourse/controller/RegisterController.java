package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setContentType(request);
        response.setHttpResponseBody(request.getUrlPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        InMemoryUserRepository.save(new User(
                request.findRequestBodyBy("account"),
                request.findRequestBodyBy("password"),
                request.findRequestBodyBy("email")
        ));
        log.info("savedUser = {}", InMemoryUserRepository.findByAccount(request.findRequestBodyBy("account")));
        response.addHttpResponseHeader("Location", "/index.html");
        response.setHttpStatusCode(302);
        response.setHttpStatusMessage("FOUND");
        response.setContentType(request);
        response.setHttpResponseBody(request.getUrlPath());
    }
}

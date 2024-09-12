package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        InMemoryUserRepository.save(createUser(request));

        response.setResponseFromRequest(request);
        response.addHttpStatus(HttpStatus.FOUND);
        response.addHeader(HttpHeaders.LOCATION, "/index.html");
    }

    private User createUser(HttpRequest request) {
        String account = request.findRequestBodyValue("account");
        String email = request.findRequestBodyValue("email");
        String password = request.findRequestBodyValue("password");
        return new User(account, password, email);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponseFromRequest(request);
        response.addHttpStatus(HttpStatus.OK);
    }
}

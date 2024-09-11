package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String requestBody = request.getBody();
        String account = requestBody.split("&")[0].split("=")[1];
        String email = requestBody.split("&")[1].split("=")[1];
        String password = requestBody.split("&")[2].split("=")[1];

        InMemoryUserRepository.save(new User(account, password, email));

        response.setResponseFromRequest(request);
        response.addHttpStatus(HttpStatus.FOUND);
        response.addHeader(HttpHeaders.LOCATION, "/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponseFromRequest(request);
        response.addHttpStatus(HttpStatus.OK);
    }
}

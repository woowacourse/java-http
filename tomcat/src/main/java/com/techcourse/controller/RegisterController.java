package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
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
        String responseBody = new String(request.toHttpResponseBody());

        response.addVersion(request.getVersion());
        response.addStatusCode(302);
        response.addStatusMessage("FOUND");
        response.addHeader("Content-Type", request.getContentType());
        response.addHeader("Content-Length", responseBody.getBytes().length);
        response.addHeader("Location", "/index.html");
        response.addBody(responseBody);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String responseBody = new String(request.toHttpResponseBody());

        response.addVersion(request.getVersion());
        response.addStatusCode(200);
        response.addStatusMessage("OK");
        response.addHeader("Content-Type", request.getContentType());
        response.addHeader("Content-Length", responseBody.getBytes().length);
        response.addBody(responseBody);
    }
}

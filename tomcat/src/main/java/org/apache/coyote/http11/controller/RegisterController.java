package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.request.UserRegisterManager;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController{

    @Override
    protected void doGet(HttpRequest request, HttpResponse response, RequestLine requestLine) throws Exception {
        response.sendResponse(response.getResponse(requestLine.getPath()));
        response.sendFile(requestLine.getPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        UserRegisterManager userRegisterManager = UserRegisterManager.of(request.getRequestBody());

        if (userRegisterManager.existsUserByAccount()) {
            response.sendResponse(response.buildRedirectHeaders("/register.html"));
            return;
        }

        userRegisterManager.saveUser();
        response.sendResponse(response.buildRedirectHeaders("index.html"));
    }
}

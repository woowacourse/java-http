package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class RegisterController extends AbstractController {

    private final UserService userService = UserService.getInstance();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> keyValueBodies = request.getKeyValueBodies();

        String account = keyValueBodies.get("account");
        String password = keyValueBodies.get("password");
        String email = keyValueBodies.get("email");

        userService.registerUser(account, password, email);

        String path = "static/index.html";

        response.setStatusLine(HttpStatus.OK);
        response.setStaticBody(path);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = "static/register.html";

        response.setStatusLine(HttpStatus.OK);
        response.setStaticBody(path);
    }
}

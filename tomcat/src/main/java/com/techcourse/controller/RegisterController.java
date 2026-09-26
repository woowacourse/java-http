package com.techcourse.controller;

import com.techcourse.service.RegisterService;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException, URISyntaxException {
        String body = registerService.readRegisterPage();
        if (body == null) {
            return HttpResponse.create("404 Not Found", "text/plain", "Not Found");
        }
        return HttpResponse.create("200 OK", "text/html", body);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getFormParameter("account");
        String password = request.getFormParameter("password");
        String email = request.getFormParameter("email");
        if (!registerService.register(account, password, email)) {
            return HttpResponse.create("400 Bad Request", "text/plain", "Missing required fields");
        }
        return HttpResponse.redirect("/index.html");
    }
}

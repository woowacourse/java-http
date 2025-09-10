package com.techcourse.controller;

import com.techcourse.service.RegisterService;
import java.util.Map;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(final RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public boolean isProvide(final String path) {
        return "/register".equals(path);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setOk("register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> bodyElement = request.getBodyElement();

        final String account = bodyElement.get("account");
        final String password = bodyElement.get("password");
        final String email = bodyElement.get("email");

        registerService.register(account, password, email);

        response.setFound("index.html");
    }
}

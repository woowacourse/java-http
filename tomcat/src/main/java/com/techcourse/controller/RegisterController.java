package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class RegisterController extends AbstractController {

    private static final String URI = "/register";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String DEFAULT_PATH = "static";
    private static final String INDEX_HTML = "/index.html";
    private static final String REGISTER_HTML = "/register.html";

    private final UserService userService = UserService.getInstance();

    public RegisterController() {
        super(URI);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        register(request);

        String path = DEFAULT_PATH + INDEX_HTML;

        response.setStatusLine(HttpStatus.OK);
        response.setStaticBody(path);
    }

    private void register(HttpRequest request) {
        Map<String, String> keyValueBodies = request.getKeyValueBodies();

        String account = keyValueBodies.get(ACCOUNT);
        String password = keyValueBodies.get(PASSWORD);
        String email = keyValueBodies.get(EMAIL);

        userService.registerUser(account, password, email);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = DEFAULT_PATH + REGISTER_HTML;

        response.setStatusLine(HttpStatus.OK);
        response.setStaticBody(path);
    }
}

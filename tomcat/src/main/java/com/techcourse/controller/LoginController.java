package com.techcourse.controller;

import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.io.IOException;
import org.apache.coyote.http11.handler.HttpHandler;
import org.apache.coyote.http11.handler.ViewHttpHandler;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService service;

    public LoginController(UserService service) {
        this.service = service;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        if (!request.hasQueryString()) {
            return new ViewHttpHandler("login").handle(request);
        }
        String account = request.getQueryParameter("account");
        String password = request.getQueryParameter("password");

        try {
            User user = service.findUserByAccountAndPassword(account, password);

            log.info("user: {}", user);

            // TODO: 빌더로 생성해보기
            HttpResponse response = HttpResponse.from(HttpStatus.FOUND);
            response.setHeader("Location", "http://localhost:8080/index.html");

            return response;
        } catch (UnauthorizedException e) {
            // TODO: 예외 처리 한곳에 모으기
            HttpResponse response = HttpResponse.from(HttpStatus.FOUND);
            response.setHeader("Location", "http://localhost:8080/401.html");

            return response;
        }
    }
}

package com.techcourse.controller;

import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.request.Api;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import com.techcourse.service.UserService;

public class UserController {

    private final Map<Api, BiConsumer<HttpRequest, HttpResponse>> supports = Map.of(
        new Api(HttpMethod.GET, "/login"), this::loginPage,
        new Api(HttpMethod.POST, "/login"), this::login,
        new Api(HttpMethod.GET, "/register"), this::registerPage,
        new Api(HttpMethod.POST, "/register"), this::register
    );

    private final UserService userService;

    public UserController(SessionManager sessionManager) {
        userService = new UserService(sessionManager);
    }

    public BiConsumer<HttpRequest, HttpResponse> getHandlerMethod(Api requestApi) {
        return supports.get(requestApi);
    }

    public void login(HttpRequest request, HttpResponse response) {
        userService.login(request, response);
        response.setHttpStatus(HttpStatus.FOUND);
        response.getHeaders().put("Location", "/index.html");
    }

    public void register(HttpRequest request, HttpResponse response) {
        userService.register(request);
        response.setHttpStatus(HttpStatus.FOUND);
        response.getHeaders().put("Location", "/index.html");
    }

    public void loginPage(HttpRequest request, HttpResponse response) {
        if (userService.getLoggedUser(request.getCookies()) == null) {
            request.setPath("/login.html");
        } else {
            request.setPath("/index.html");
        }
    }

    public void registerPage(HttpRequest request, HttpResponse response) {
        request.setPath("/register.html");
    }
}

package com.techcourse.controller;

import java.util.Map;
import java.util.function.Function;

import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.request.Api;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class UserController {

    private static final String SESSION_NAME = "JSESSIONID";

    private final Map<Api, Function<HttpRequest, HttpResponse>> supports = Map.of(
        new Api(HttpMethod.GET, "/login"), this::loginPage,
        new Api(HttpMethod.POST, "/login"), this::login,
        new Api(HttpMethod.GET, "/register"), this::registerPage,
        new Api(HttpMethod.POST, "/register"), this::register
    );
    private final UserService userService;
    private final SessionManager sessionManager;

    public UserController(SessionManager sessionManager) {
        userService = new UserService();
        this.sessionManager = sessionManager;
    }

    public Function<HttpRequest, HttpResponse> getHandlerMethod(Api requestApi) {
        return supports.get(requestApi);
    }

    public HttpResponse login(HttpRequest request) {
        var requestBody = request.getBody();
        User user = userService.login(requestBody);
        Session session = new Session();
        session.setAttribute("user", user);
        sessionManager.add(session);

        return HttpResponse.builder()
            .status(HttpStatus.FOUND)
            .header("Location", "/index.html")
            .cookie(SESSION_NAME, session.getId())
            .build();
    }

    public HttpResponse register(HttpRequest request) {
        userService.register(request);

        return HttpResponse.builder()
            .status(HttpStatus.FOUND)
            .header("Location", "/index.html")
            .build();
    }

    public HttpResponse loginPage(HttpRequest request) {
        String sessionId = request.getCookies().get(SESSION_NAME);
        if (sessionId == null) {
            request.setPath("/index.html");
        }
        Session session = sessionManager.findSession(sessionId);
        if (userService.getLoggedUser(session) == null) {
            request.setPath("/login.html");
        } else {
            request.setPath("/index.html");
        }
        return new HttpResponse();
    }

    public HttpResponse registerPage(HttpRequest request) {
        request.setPath("/register.html");
        return new HttpResponse();
    }
}

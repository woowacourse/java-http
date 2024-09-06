package com.techcourse.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class LoginController extends Controller {
    private static final LoginController instance = new LoginController();
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService = new UserService();

    private LoginController() {
    }

    public static LoginController getInstance() {
        return instance;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        try {
            HttpResponse response = operate(request);
            return response;
        } catch (UnauthorizedException e) {
            log.error("Error processing request for endpoint: {}", request.getURI(), e);

            return redirect("401.html", new HttpResponse());
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        Map<String, String> requestBody = request.getBody().parseRequestBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");

        User user = userService.login(account, password);
        log.info("User found: {}", user);

        HttpCookie httpCookie = new HttpCookie(request.getCookie());
        if (httpCookie.hasJSessionId()) {
            return redirect("index.html", new HttpResponse());
        }
        HttpResponse response = new HttpResponse();
        Session session = Session.createRandomSession();
        session.setAttribute("user", user.getAccount());
        response.setCookie(HttpCookie.ofJSessionId(session.getId()));

        return redirect("index.html", response);
    }


    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpCookie httpCookie = new HttpCookie(request.getCookie());
        if (httpCookie.hasJSessionId()) {
            return redirect("index.html", new HttpResponse());
        }
        return redirect("login.html", new HttpResponse());
    }

    private static HttpResponse redirect(String location, HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(location);
        return response;
    }
}


package com.techcourse.controller;

import com.techcourse.controller.dto.HttpResponseEntity;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.net.URI;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;

public class UserController {

    private static final URI REDIRECT_URI = URI.create("/index.html");
    private static final URI UNAUTHORIZED_URI = URI.create("/401.html");

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public HttpResponseEntity<User> searchUserData(Map<String, String> params) {
        try {
            User user = userService.findUser(params);
            HttpResponseEntity<User> httpResponse = new HttpResponseEntity<>(HttpStatus.FOUND, user);
            httpResponse.addHeader(HttpHeaders.LOCATION, REDIRECT_URI.getPath());
            return httpResponse;
        } catch (IllegalArgumentException e) {
            HttpResponseEntity<User> responseEntity = new HttpResponseEntity<>(HttpStatus.FOUND, null);
            responseEntity.addHeader(HttpHeaders.LOCATION, UNAUTHORIZED_URI.getPath());
            return responseEntity;
        }
    }

    public HttpResponseEntity<User> login(Map<String, String> params, HttpRequest request) {
        try {
            User user = userService.findUser(params);
            HttpResponseEntity<User> responseEntity = new HttpResponseEntity<>(HttpStatus.FOUND, user);
            Session session = request.getSession(true);
            SessionManager.getInstance().add(session);
            responseEntity.addHeader(HttpHeaders.LOCATION, REDIRECT_URI.getPath());
            responseEntity.addCookie(HttpCookie.ofJSessionId(session.getId()));
            return responseEntity;
        } catch (IllegalArgumentException e) {
            HttpResponseEntity<User> httpResponse = new HttpResponseEntity<>(HttpStatus.FOUND, null);
            httpResponse.addHeader(HttpHeaders.LOCATION, UNAUTHORIZED_URI.getPath());
            return httpResponse;
        }
    }

    public HttpResponseEntity<Void> registerUser(Map<String, String> params) {
        try {
            userService.create(params);
            HttpResponseEntity<Void> responseEntity = new HttpResponseEntity<>(HttpStatus.FOUND, null);
            responseEntity.addHeader(HttpHeaders.LOCATION, REDIRECT_URI.getPath());
            return responseEntity;
        } catch (IllegalArgumentException e) {
            HttpResponseEntity<Void> responseEntity = new HttpResponseEntity<>(HttpStatus.FOUND, null);
            responseEntity.addHeader(HttpHeaders.LOCATION, UNAUTHORIZED_URI.getPath());
            return responseEntity;
        }
    }
}

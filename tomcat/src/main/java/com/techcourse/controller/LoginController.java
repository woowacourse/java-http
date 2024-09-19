package com.techcourse.controller;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.constants.ContentType;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.constants.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.LoginRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.constants.HttpStatus;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.util.StaticResourceReader;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, HttpResponse response) {
        final LoginRequest loginRequest = LoginRequest.from(request.getBody());
        final Optional<User> user = InMemoryUserRepository.findByAccount(loginRequest.getAccount());
        if (isValidUser(user, loginRequest.getPassword())) {
            successLogin(response, user);
            return;
        }
        failLogin(response);
    }

    private boolean isValidUser(Optional<User> user, String password) {
        if (user.isEmpty()) {
            System.out.println("일치하는 유저가 없습니다.");
            return false;
        }
        final boolean isMatchedPassword = user.get().checkPassword(password);
        if (isMatchedPassword) {
            return true;
        }
        System.out.println("비밀번호가 틀렸습니다.");
        return false;
    }

    private void successLogin(final HttpResponse response, final Optional<User> user) {
        final Session session = makeSession(user);
        final Cookie cookie = new Cookie(Cookie.J_SESSION_KEY + session.getId());
        response.addHeader(HttpHeader.SET_COOKIE, cookie.getValue());
        redirectIndexPage(response);
    }

    private void failLogin(final HttpResponse response) {
        response.setHttpStatus(HttpStatus.FOUND);
        response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getContentTypeUtf8());
        response.addHeader(HttpHeader.LOCATION, "/401.html");
    }

    private Session makeSession(final Optional<User> user) {
        final UUID uuid = UUID.randomUUID();
        final Session session = new Session(uuid.toString());
        session.setAttribute("user", user);
        final SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        return session;
    }

    @Override
    protected void doGet(final HttpRequest request, HttpResponse response) throws Exception {
        final Cookie cookie = request.getCookie();
        if (isLoggedIn(cookie)) {
            redirectIndexPage(response);
            return;
        }
        responseLoginPage(response);
    }

    private boolean isLoggedIn(final Cookie cookie) {
        if (cookie == null) {
            return false;
        }
        if (cookie.isJSessionCookie()) {
            return SessionManager.getInstance().hasSession(cookie.getJSessionId());
        }
        return false;
    }

    private void redirectIndexPage(final HttpResponse response) {
        response.setHttpStatus(HttpStatus.FOUND);
        response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getContentTypeUtf8());
        response.addHeader(HttpHeader.LOCATION, "/index.html");
    }

    private void responseLoginPage(final HttpResponse response) throws IOException {
        String body = StaticResourceReader.read("/login.html");
        response.setHttpStatus(HttpStatus.OK);
        response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getContentTypeUtf8());
        response.setBody(body);
    }
}

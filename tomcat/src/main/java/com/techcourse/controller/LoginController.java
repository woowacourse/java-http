package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> body = request.getRequestBody().getBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(body.get("account"));
        if (user.isPresent()) {
            processLogin(request, response, user.get());
            return;
        }
        response.setLoginFail(request.getRequestLine().getProtocol());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (alreadyLogin(request)) {
            response.successLogin(request.getRequestLine().getProtocol());
            return;
        }
        response.setLoginPage(request.getRequestLine().getProtocol(), request.getUrl());
    }

    private void processLogin(HttpRequest request, HttpResponse response, User user) {
        if (user.checkPassword(request.getRequestBody().getBody().get("password"))) {
            response.successLogin(request.getRequestLine().getProtocol());
            setSession(user, response);
        } else {
            response.failLogin(request.getRequestLine().getProtocol());
        }
    }

    private void setSession(User user, HttpResponse response) {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        session.setAttribute(user.getAccount(), user);
        SessionManager.getInstance().add(session);
        response.putHeader("Set-Cookie", "JSESSIONID=" + session.getId());
    }

    private boolean alreadyLogin(HttpRequest request) {
        String cookie = request.httpHeader().getCookie();
        if (cookie != null) {
            String jSessionId = new HttpCookie(cookie).getJSessionId();
            Session session = SessionManager.getInstance().findSession(jSessionId);
            if (session != null) {
                return true;
            }
        }
        return false;
    }


}

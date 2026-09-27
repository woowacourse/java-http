package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class LoginController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (!request.hasHeader("Cookie")) {
            return;
        }
        HttpCookie httpCookie = new HttpCookie(request.getHeaderValue("Cookie"));
        String jSessionId = httpCookie.get("JSESSIONID");

        SessionManager manager = SessionManager.getInstance();

        if (manager.findSession(jSessionId) != null) {
            response.setStatus("302 FOUND");
            response.addHeader("Location", "/index.html");
        }
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        if (request.hasBody()) {
            login(request, response);
        }
    }

    private void login(HttpRequest request, HttpResponse response) {
        String jSessionId = "";
        if (request.hasHeader("Cookie")) {
            HttpCookie httpCookie = new HttpCookie(request.getHeaderValue("Cookie"));
            jSessionId = httpCookie.get("JSESSIONID");
        }

        SessionManager manager = SessionManager.getInstance();

        String account = request.getBodyValue("account");
        String password = request.getBodyValue("password");
        if (userMatching(account, password)) {
            if (manager.findSession(jSessionId) == null) {
                addSession(account, response, manager);
            }
            response.addHeader("Location", "/index.html");
        } else {
            response.addHeader("Location", "/401.html");
        }
        response.setStatus("302 FOUND");
    }

    private void addSession(String account, HttpResponse response, SessionManager manager) {
        String jSessionId = UUID.randomUUID().toString();
        Session session = new Session(jSessionId);
        boolean isPresent = InMemoryUserRepository.findByAccount(account).isPresent();
        if (isPresent) {
            User user = InMemoryUserRepository.findByAccount(account).get();
            session.setAttribute("user", user);
            manager.add(session);
            response.addHeader("Set-Cookie", "JSESSIONID=" + jSessionId + " ");
        }
    }

    private boolean userMatching(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }
}

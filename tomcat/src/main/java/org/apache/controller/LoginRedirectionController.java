package org.apache.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.http.Cookie;
import org.apache.http.HttpMethod;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusCode;
import org.apache.session.Session;
import org.apache.session.SessionManager;

public class LoginRedirectionController implements Controller {

    @Override
    public boolean isProcessableRequest(HttpRequest request) {
        return request.getMethod() == HttpMethod.GET
                && request.getUri().equals("/login")
                && request.checkCookieExistence("JSESSIONID");
    }

    @Override
    public void processRequest(HttpRequest request, HttpResponse response) {
        Cookie cookie = request.getCookie("JSESSIONID");
        Session session = SessionManager.findSession(cookie.getValue());
        if (session != null && isValidUser(session.getUser())) {
            response.setStatusCode(StatusCode.FOUND);
            response.setHeader("Location", "/index.html");
        }
    }

    private boolean isValidUser(User user) {
        return InMemoryUserRepository
                .findByAccount(user.getAccount())
                .isPresent();
    }
}


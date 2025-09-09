package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import java.util.Map;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.constant.ContentType;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.util.FileReader;

public class LoginController extends AbstractController {

    private final SessionManager sessionManager = new SessionManager();

    public LoginController() {
        super("/login");
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.containsCookie() && httpRequest.getCookie().containsJSessionId()) {
            httpResponse.setResponse(HttpStatus.FOUND, ContentType.TEXT);
            httpResponse.appendHeader("Location", "http://localhost:8080/index.html");
            return;
        }
        httpResponse.setResponse(HttpStatus.OK, ContentType.HTML, FileReader.readFile("login.html"));
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (!isValidUser(httpRequest)) {
            loginFailedResponse(httpResponse);
            return;
        }
        final Session session = sessionManager.generateSession();
        httpResponse.setResponse(HttpStatus.FOUND, ContentType.HTML);
        httpResponse.setCookie(Map.of("JSESSIONID", session.getId()));
        httpResponse.setRedirect("http://localhost:8080/index.html");
    }

    private boolean isValidUser(HttpRequest httpRequest) {
        final Map<String, String> body = httpRequest.parseBody();
        final String account = body.get("account");
        final String password = body.get("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }

    private void loginFailedResponse(HttpResponse httpResponse) {
        httpResponse.setResponse(HttpStatus.UNAUTHORIZED, ContentType.HTML);
        httpResponse.setRedirect("http://localhost:8080/401.html");
    }
}

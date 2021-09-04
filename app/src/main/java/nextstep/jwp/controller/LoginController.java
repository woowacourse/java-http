package nextstep.jwp.controller;

import java.util.NoSuchElementException;
import java.util.UUID;

import nextstep.jwp.exception.LoginException;
import nextstep.jwp.handler.Cookie;
import nextstep.jwp.handler.HttpSession;
import nextstep.jwp.handler.HttpSessions;
import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.handler.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;

public class LoginController extends AbstractController {


    public static final String INDEX_HTML = "/index.html";

    private final LoginService loginService;

    public LoginController() {
        this.loginService = new LoginService();
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            HttpSession session = httpRequest.getSession();
            if (session.containsAttribute("user")) {
                httpResponse.redirect(INDEX_HTML);
                return;
            }
        } catch (NoSuchElementException e) {
            httpResponse.ok(httpRequest.getRequestUrl() + ".html");
            return;
        }

        if (httpRequest.isUriContainsQuery()) {
            doGetWithQuery(httpRequest, httpResponse);
            return;
        }
        httpResponse.ok(httpRequest.getRequestUrl() + ".html");
    }

    private void doGetWithQuery(HttpRequest httpRequest, HttpResponse httpResponse) {}

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            User user = loginService.login(httpRequest);

            if (httpRequest.containsCookie("JSESSIONID")) {
                final HttpSession session = httpRequest.getSession();
                session.setAttribute("user", user);

                httpResponse.redirect(INDEX_HTML);
                return;
            }

            String sessionId = saveSession();
            HttpSession session = HttpSessions.getSession(sessionId);
            session.setAttribute("user", user);

            httpResponse.setCookie(new Cookie("JSESSIONID", sessionId));
            httpResponse.redirect(INDEX_HTML);
        } catch (LoginException | NoSuchElementException e) {
            httpResponse.unauthorized("/401.html");
        }
    }

    private String saveSession() {
        String sessionId = UUID.randomUUID().toString();
        HttpSessions.add(sessionId);
        return sessionId;
    }
}

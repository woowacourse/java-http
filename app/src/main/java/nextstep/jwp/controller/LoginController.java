package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.model.User;

import java.util.Map;

public class LoginController extends AbstractController{
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> requestBody = request.getRequestBody();
        if (requestBody.size() > 0) {
            String account = request.getParameter("account");
            String password = request.getParameter("password");

            User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
            validateUserPassword(request, response, user, password);
            response.redirect("/401.html");
        }
        response.forward("/login.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("user") == null) {
            response.forward("/login.html");
            return;
        }
        response.forward("/index.html");
    }

    private void validateUserPassword(HttpRequest request, HttpResponse response, User user, String password) {
        if (user.checkPassword(password)) {
            HttpSession session = findOrCreateSession(request, response);
            session.setAttribute("user", user);
            request.setSession(session);
            response.redirect("/index.html");
        }
    }

    private HttpSession findOrCreateSession(HttpRequest request, HttpResponse response) {
        String cookie = request.getHeader("Cookie");
        if (cookie == null || request.getCookies().get("JSESSIONID") == null) {
            HttpSession session = HttpSessions.createSession();
            HttpSessions.SESSIONS.put(session.getId(), session);
            response.setSession(session.getId());
            return session;
        }
        String sessionId = request.getCookies().get("JSESSIONID");
        return HttpSessions.getOrCreateSession(sessionId);
    }
}

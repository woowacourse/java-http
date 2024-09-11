package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

    private static final LoginController instance = new LoginController();
    private final SessionManager sessionManager = SessionManager.getInstance();

    private LoginController() {}

    public static LoginController getInstance() {
        return instance;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> pairs = request.getBodyQueryString();

        String account = pairs.get("account");
        String password = pairs.get("password");
        if (account != null & password != null & InMemoryUserRepository.doesExistAccount(account)) {
            User user = InMemoryUserRepository.findByAccount(account).get();
            if (user.checkPassword(password)) {
                Session session = new Session(UUID.randomUUID().toString());
                session.addAttribute("user", user);
                sessionManager.add(session);
                redirectToHomeSettingCookie(response, session.getId());
                return;
            }
        }
        redirectTo(response, "/401.html");
    }

    private void redirectToHomeSettingCookie(HttpResponse response, String jSessionId) throws IOException {
        response.addStatusLine("HTTP/1.1 302 Found");
        response.addHeader("Set-Cookie", "JSESSIONID=" + jSessionId);
        response.addHeader("Location", "http://localhost:8080/index.html");
        response.writeResponse();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (doesLoggedIn(request.getCookie())) {
            redirectTo(response, "/index.html");
            return;
        }
        serveStaticFile(request, response);
    }

    private boolean doesLoggedIn(HttpCookie httpCookie) {
        Session session = sessionManager.findSession(httpCookie.get("JSESSIONID"));
        return session != null && session.doesExistAttribute("user");
    }
}

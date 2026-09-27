package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Session session = findSession(request);

        if (session == null) {
            session = new Session(UUID.randomUUID().toString());
            SessionManager.getInstance().add(session);
        }

        Map<String, String> loginInfo = new HashMap<>();

        for (String parameter : request.getBody().split("&")) {
            addDecodedParameter(loginInfo, parameter);
        }

        writeLoginResponse(response, session, loginInfo);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Session session = findSession(request);
        String queryString = request.getQueryString();

        if (queryString.isEmpty()) {
            if (session != null && getLoggedInUser(session) != null) {
                response.setStatus(302, "Found");
                response.setBody("");
                response.addHeader("Location", "/index.html");
                response.addHeader("Content-Length", "0");
                return;
            }

            URL resourceUrl = getClass().getClassLoader().getResource("static/login.html");

            if (resourceUrl == null) {
                throw new IllegalArgumentException("login.html 리소스를 찾을 수 없습니다.");
            }

            String loginPage = Files.readString(Paths.get(resourceUrl.toURI()), StandardCharsets.UTF_8);

            response.setBody(loginPage);
            response.addHeader("Content-Type", "text/html;charset=utf-8");
            response.addHeader("Content-Length", String.valueOf(loginPage.getBytes(StandardCharsets.UTF_8).length));
            return;
        }

        if (session == null) {
            session = new Session(UUID.randomUUID().toString());
            SessionManager.getInstance().add(session);
        }

        Map<String, String> loginInfo = new HashMap<>();

        for (String parameter : queryString.split("&")) {
            addDecodedParameter(loginInfo, parameter);
        }

        writeLoginResponse(response, session, loginInfo);
    }

    private Session findSession(HttpRequest request) {
        HttpCookie requestCookie = new HttpCookie(request.getHeader("cookie"));
        String sessionId = requestCookie.get(HttpCookie.JSESSIONID);
        return SessionManager.getInstance().findSession(sessionId);
    }

    private User getLoggedInUser(Session session) {
        return (User) session.getAttribute("user");
    }

    private boolean authenticate(Session session, Map<String, String> loginInfo) {
        String account = loginInfo.get("account");
        String password = loginInfo.get("password");

        if (account != null && !account.isBlank() && password != null && !password.isBlank()) {
            var optionalUser = InMemoryUserRepository.findByAccount(account);

            if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
                session.setAttribute("user", optionalUser.get());
                return true;
            }
        }

        return false;
    }

    private void writeLoginResponse(HttpResponse response, Session session, Map<String, String> loginInfo) {
        boolean loginSuccess = authenticate(session, loginInfo);

        response.setStatus(302, "Found");
        response.setBody("");
        response.addHeader("Location", loginSuccess ? "/index.html" : "/401.html");
        response.addHeader("Content-Length", "0");

        if (loginSuccess) {
            HttpCookie responseCookie = HttpCookie.ofJSessionId(session.getId());
            response.addHeader("Set-Cookie", responseCookie.toHeaderValue());
        }
    }

    private static void addDecodedParameter(Map<String, String> loginInfo, String parameter) {
        String[] keyValue = parameter.split("=", 2);

        if (keyValue.length != 2) {
            return;
        }

        String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
        String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

        loginInfo.put(key, value);
    }
}

package org.apache.coyote.handler;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpCookie;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.Session;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponseGenerator;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginHandler {
    private static final LoginHandler INSTANCE = new LoginHandler();
    private final SessionManager sessionManager = SessionManager.getInstance();

    private LoginHandler() {
    }

    public static LoginHandler getInstance() {
        return INSTANCE;
    }

    public String processLoginRequest(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isSameMethod(HttpMethod.GET)) {
            return processLoginGetRequest(httpRequest);
        }

        if (httpRequest.isSameMethod(HttpMethod.POST)) {
            return processLoginPostRequest(httpRequest);
        }

        return ResourceHandler.getInstance().handleSimpleResource("404.html");

    }

    private String processLoginGetRequest(final HttpRequest httpRequest) throws IOException {
        HttpCookie httpCookie = httpRequest.getHttpCookie();
        if (httpCookie == null) {
            return ResourceHandler.getInstance().handleSimpleResource("login.html");
        }

        String sessionId = httpCookie.getValue("JSESSIONID=");
        if (sessionManager.findSession(sessionId) == null) {
            return ResourceHandler.getInstance().handleSimpleResource("login.html");
        }
        return HttpResponseGenerator.getFoundResponse("http://localhost:8080/index.html");
    }

    private String processLoginPostRequest(final HttpRequest httpRequest) throws IOException {
        final String[] params = httpRequest.getBody().split("&");
        final String account = params[0].split("=")[1];
        final String password = params[1].split("=")[1];

        final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isEmpty()) {
            return ResourceHandler.getInstance().handleSimpleResource("401.html");
        }

        final User user = userOptional.get();
        if (user.checkPassword(password)) {
            final Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);
            sessionManager.add(session);
            return addCookie(
                    HttpResponseGenerator.getFoundResponse("http://localhost:8080/index.html"),
                    new HttpCookie("JSESSIONID=" + session.getId()));
        }

        return ResourceHandler.getInstance().handleSimpleResource("404.html");
    }

    private String addCookie(final String response, final HttpCookie cookie) {
        return response.concat("\n").concat("Set-Cookie: " + cookie.toString());
    }
}

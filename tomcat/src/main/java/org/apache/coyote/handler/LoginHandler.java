package org.apache.coyote.handler;

import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.SessionManager;
import org.apache.http.HttpCookie;
import org.apache.http.HttpMethod;
import org.apache.coyote.Session;
import org.apache.coyote.mapping.ResourceHandlerMapping;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponseGenerator;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginHandler extends Handler {

    private static final LoginHandler INSTANCE = new LoginHandler();
    private final SessionManager sessionManager = SessionManager.getInstance();

    private LoginHandler() {
    }

    public static LoginHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public String handle(final HttpRequest httpRequest) {
        if (httpRequest.isSameMethod(HttpMethod.GET)) {
            return processLoginGetRequest(httpRequest);
        }

        if (httpRequest.isSameMethod(HttpMethod.POST)) {
            return processLoginPostRequest(httpRequest);
        }

        return ResourceHandlerMapping.getInstance().handleSimpleResource("404.html");

    }

    private String processLoginGetRequest(final HttpRequest httpRequest) {
        HttpCookie httpCookie = httpRequest.getHttpCookie();
        if (httpCookie == null) {
            return ResourceHandlerMapping.getInstance().handleSimpleResource("login.html");
        }

        String sessionId = httpCookie.getValue("JSESSIONID=");
        if (sessionManager.findSession(sessionId) == null) {
            return ResourceHandlerMapping.getInstance().handleSimpleResource("login.html");
        }
        return HttpResponseGenerator.getFoundResponse("http://localhost:8080/index.html");
    }

    private String processLoginPostRequest(final HttpRequest httpRequest) {
        final String[] params = httpRequest.getBody().split("&");
        final String account = params[0].split("=")[1];
        final String password = params[1].split("=")[1];

        final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isEmpty()) {
            return ResourceHandlerMapping.getInstance().handleSimpleResource("401.html");
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

        return ResourceHandlerMapping.getInstance().handleSimpleResource("404.html");
    }

    private String addCookie(final String response, final HttpCookie cookie) {
        return response.concat("\n").concat("Set-Cookie: " + cookie.toString());
    }
}

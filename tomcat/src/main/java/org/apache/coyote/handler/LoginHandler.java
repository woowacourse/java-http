package org.apache.coyote.handler;

import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.session.SessionManager;
import org.apache.http.HttpCookie;
import org.apache.http.HttpMethod;
import org.apache.catalina.session.Session;
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

        return StaticResourceHandler.getInstance().handle(new HttpRequest("GET", "/404.html", "HTTP/1.1", null, null));

    }

    private String processLoginGetRequest(final HttpRequest httpRequest) {
        HttpCookie httpCookie = httpRequest.getHttpCookie();
        if (httpCookie == null) {
            return StaticResourceHandler.getInstance().handle(new HttpRequest("GET", "/login.html", "HTTP/1.1", null, null));
        }

        String sessionId = httpCookie.getValue("JSESSIONID=");
        if (sessionManager.findSession(sessionId) == null) {
            return StaticResourceHandler.getInstance().handle(new HttpRequest("GET", "/login.html", "HTTP/1.1", null, null));
        }
        return HttpResponseGenerator.getFoundResponse("http://localhost:8080/index.html");
    }

    private String processLoginPostRequest(final HttpRequest httpRequest) {
        final String[] params = httpRequest.getBody().split("&");
        final String account = params[0].split("=")[1];
        final String password = params[1].split("=")[1];

        final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isEmpty()) {
            return StaticResourceHandler.getInstance().handle(new HttpRequest("GET", "/401.html", "HTTP/1.1", null, null));
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

        return StaticResourceHandler.getInstance().handle(new HttpRequest("GET", "/404.html", "HTTP/1.1", null, null));
    }

    private String addCookie(final String response, final HttpCookie cookie) {
        return response.concat("\n").concat("Set-Cookie: " + cookie.toString());
    }
}

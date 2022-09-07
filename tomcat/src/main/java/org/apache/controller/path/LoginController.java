package org.apache.controller.path;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.request.HttpRequest;
import org.apache.request.RequestLine;
import org.apache.request.RequestUri;
import org.apache.response.HttpResponse;
import org.apache.util.QueryStringParser;
import session.Session;
import session.SessionManager;

public class LoginController implements PathController {

    private static LoginController loginController = new LoginController();

    private final String path = "/login";

    public static LoginController getInstance() {
        return loginController;
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestLine requestLine = httpRequest.getRequestLine();
        String method = requestLine.getMethod();
        String body = httpRequest.getBody();

        if (method.equals("GET")) {
            if (isAlreadyLogin(httpRequest.getHeaderValue("Cookie"))) {
                httpResponse.set302Redirect("http://localhost:8080/index.html");
                return;
            }
            httpResponse.setStaticResource(new RequestUri("/login.html"));
        }

        if (method.equals("POST")) {
            login(httpResponse, body);
        }
    }

    private boolean isAlreadyLogin(final Optional<String> value) {
        return value.isPresent() && SessionManager.containJSessionId(value.get());
    }

    private void login(final HttpResponse httpResponse, final String body) {
        Map<String, String> queryString = QueryStringParser.parseQueryString(body);
        String account = queryString.get("account").trim();
        String password = queryString.get("password").trim();
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            httpResponse.set302Redirect("http://localhost:8080/index.html");
            putCookies(httpResponse, user.get());
        } else {
            httpResponse.set302Redirect("http://localhost:8080/401.html");
        }
    }

    private void putCookies(final HttpResponse httpResponse, final User user) {
        Session session = new Session();
        session.addCookie("JSESSIONID", UUID.randomUUID().toString());
        SessionManager.addSession(user, session);
        httpResponse.putHeader("Set-Cookie", session.findAllCookies());
    }

    @Override
    public String getPath() {
        return path;
    }
}

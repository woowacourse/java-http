package org.apache.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.http.HttpCookie;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    private final StaticController staticController = new StaticController();

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        return httpRequest.pathEquals("/login");
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest, HttpResponse httpResponse)
            throws URISyntaxException, IOException {
        if (httpRequest.getMethod().equals("GET")) {
            return getLogin(httpRequest, httpResponse);
        }
        return postLogin(httpRequest, httpResponse);
    }

    private HttpResponse getLogin(HttpRequest httpRequest, HttpResponse httpResponse)
            throws IOException, URISyntaxException {
        if (httpRequest.containsCookie()) {
            HttpCookie httpCookie = httpRequest.getHttpCookie();

            String jsessionID = httpCookie.getJSessionId();

            if (isAlreadyLogin(jsessionID)) {
                httpResponse.redirect("/login.html");
                return httpResponse;
            }
        }
        httpResponse.setResponseBody("/login.html");
        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setHttpCookie(null);

        return staticController.process(httpRequest, httpResponse);
    }

    private boolean isAlreadyLogin(String jsessionID) throws IOException {
        Session session = SESSION_MANAGER.findSession(jsessionID);
        return session != null && session.getAttribute("user") != null;
    }

    private HttpResponse postLogin(HttpRequest httpRequest, HttpResponse httpResponse)
            throws URISyntaxException, IOException {
        String account = httpRequest.getBodyAttribute("account");
        String password = httpRequest.getBodyAttribute("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);

        if (!user.checkPassword(password)) {
            return postLoginFailed(httpRequest, httpResponse);
        }

        if (user.checkPassword(password)) {
            return postLoginSuccess(user, httpRequest, httpResponse);
        }

        return HttpResponse.notFound(httpRequest);
    }

    private HttpResponse postLoginFailed(HttpRequest httpRequest, HttpResponse httpResponse)
            throws IOException, URISyntaxException {
        httpResponse.setResponseBody("/401.html");
        httpResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
        httpResponse.setHttpCookie(null);
        return staticController.process(httpRequest, httpResponse);
    }

    private HttpResponse postLoginSuccess(User user, HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpCookie httpCookie = new HttpCookie();
        String sessionId = UUID.randomUUID().toString();

        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);

        httpCookie.setjSessionId(sessionId);
        httpResponse.setHttpCookie(httpCookie);
        log.info("user: {}", user);
        httpResponse.redirect("/index.html");

        return httpResponse;
    }
}

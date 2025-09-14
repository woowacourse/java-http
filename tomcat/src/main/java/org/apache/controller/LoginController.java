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
import org.apache.view.ViewUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

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
                httpResponse.redirect("/index.html");
                return httpResponse;
            }
        }

        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setHttpCookie(null);

        return ViewUtils.render(httpResponse, "/login.html");
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
            return postLoginFailed(httpResponse);
        }

        if (user.checkPassword(password)) {
            return postLoginSuccess(user, httpResponse);
        }

        return HttpResponse.notFound();
    }

    private HttpResponse postLoginFailed(HttpResponse httpResponse)
            throws IOException, URISyntaxException {
        httpResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
        httpResponse.setHttpCookie(null);
        return ViewUtils.render(httpResponse, "/401.html");
    }

    private HttpResponse postLoginSuccess(User user, HttpResponse httpResponse) {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);

        HttpCookie httpCookie = new HttpCookie();
        httpCookie.setjSessionId(sessionId);
        httpResponse.setHttpCookie(httpCookie);

        log.info("user: {}", user);
        httpResponse.redirect("/index.html");

        return httpResponse;
    }
}

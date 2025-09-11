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
    private static final StaticController staticController = new StaticController();
    SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        return httpRequest.pathEquals("/login");
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest, HttpResponse httpResponse)
            throws URISyntaxException, IOException {

        return login(httpRequest, httpResponse);
    }

    private HttpResponse login(HttpRequest httpRequest, HttpResponse httpResponse)
            throws URISyntaxException, IOException {

        if (httpRequest.getMethod().equals("GET")) {
            if (httpRequest.containsCookie()) {
                HttpCookie httpCookie = httpRequest.getHttpCookie();

                String jsessionID = httpCookie.getJSessionId();
                Session session = sessionManager.findSession(jsessionID);

                if (session != null && session.getAttribute("user") != null) {
                    httpResponse.redirect("/login.html");
                    return httpResponse;
                }
            }
            httpResponse.setResponseBody("/login.html");
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.setHttpCookie(null);

            return staticController.process(httpRequest, httpResponse);
        }

        String account = httpRequest.getBodyAttribute("account");
        String password = httpRequest.getBodyAttribute("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);

        if (!user.checkPassword(password)) {
            httpResponse.setResponseBody("/401.html");
            httpResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
            httpResponse.setHttpCookie(null);
            return staticController.process(httpRequest, httpResponse);
        }

        if (user.checkPassword(password)) {
            HttpCookie httpCookie = new HttpCookie();
            String sessionId = UUID.randomUUID().toString();

            Session session = new Session(sessionId);
            session.setAttribute("user", user);
            sessionManager.add(session);

            httpCookie.setjSessionId(sessionId);
            httpResponse.setHttpCookie(httpCookie);
            log.info("user: {}", user);
            httpResponse.redirect("/index.html");

            return httpResponse;
        }

        return HttpResponse.notFound(httpRequest);
    }
}

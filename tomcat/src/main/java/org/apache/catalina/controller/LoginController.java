package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.message.HttpCookie;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.StatusLine;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (request.hasSessionCookie()) {
            final Session session = sessionManager.findSession(request.getJsessionId());
            if (session != null && session.getAttribute("user") != null) {
                redirect(request, response, "/index.html");
            }
        }

        renderPage(request, response, "/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        final Map<String, String> params = request.getFormParams();

        final String account = params.get("account");
        final String password = params.get("password");

        if (account == null || password == null) {
            renderPage(request, response, "/login.html");
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> {
                            loginUser(request, response, user);
                            log.info("user: %s", user);
                        },
                        () -> redirect(request, response, "/401.html")
                );
    }

    private void loginUser(
            final HttpRequest request,
            final HttpResponse response,
            final User user
    ) {
        final Session session = new Session();
        session.setAttribute("user", user);
        sessionManager.add(session);

        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Location", "/index.html");

        final HttpCookie cookie = HttpCookie.of("JSESSIONID", session.getId());
        headers.addHeader("Set-Cookie", cookie.toHeaderCookie());
        headers.addHeader("Content-Length", "0");

        response.setStatusLine(new StatusLine(request.getVersion(), StatusCode.FOUND));
        response.setHeaders(headers);
        response.setBody(new byte[0]);
    }
}

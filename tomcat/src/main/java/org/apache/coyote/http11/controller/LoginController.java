package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.HttpCookie;
import org.apache.coyote.http11.http.domain.MessageBody;
import org.apache.coyote.http11.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            MessageBody messageBody = httpRequest.getMessageBody();
            User user = findUser(messageBody);
            String password = messageBody.getParameter("password");
            login(user, password, httpResponse);
        } catch (NotFoundException e) {
            httpResponse.found()
                    .location("/401.html")
                    .flushBuffer();
        }
    }

    private User findUser(final MessageBody messageBody) {
        String account = messageBody.getParameter("account");
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("User not found."));
    }

    private void login(final User user, final String password, final HttpResponse httpResponse) {
        Session session = createSession(user);
        if (user.checkPassword(password)) {
            SessionManager.add(session);
            log.info("User Login : {}", user);
            httpResponse.found()
                    .setCookie(session)
                    .location("/index.html")
                    .flushBuffer();
            return;
        }
        httpResponse.found()
                .location("/401.html")
                .flushBuffer();
    }

    private Session createSession(final User user) {
        Session session = Session.newSession();
        session.setAttribute("user", user);
        return session;
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        HttpCookie cookie = httpRequest.getHeaders().getCookie();
        String jsessionid = cookie.getCookie("JSESSIONID");
        if (isLoggedIn(cookie, jsessionid)) {
            redirectToHomePage(cookie, httpResponse);
            return;
        }
        getLoginPage(httpRequest, httpResponse);
    }

    private boolean isLoggedIn(final HttpCookie cookie, final String jsessionid) {
        return cookie.containsJSESSIONID() && SessionManager.contains(jsessionid);
    }

    private void redirectToHomePage(final HttpCookie cookie, final HttpResponse httpResponse) {
        String jsessionid = cookie.getCookie("JSESSIONID");
        Session session = SessionManager.findSession(jsessionid);
        User user = (User) session.getAttribute("user");
        log.info("Login User : {}", user);
        httpResponse.found()
                .location("/index.html")
                .flushBuffer();
    }

    private void getLoginPage(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String uri = httpRequest.getUri();
        String responseBody = FileReader.read(uri + ".html");
        httpResponse.ok()
                .contentType(ContentType.from(uri))
                .body(responseBody)
                .flushBuffer();
    }
}

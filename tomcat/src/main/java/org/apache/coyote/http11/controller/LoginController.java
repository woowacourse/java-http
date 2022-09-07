package org.apache.coyote.http11.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.Headers;
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
            Map<String, String> parameters = httpRequest.getMessageBody()
                    .getParameters();
            String account = parameters.get("account");
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new NotFoundException("User not found."));
            String password = parameters.get("password");
            login(user, password, httpResponse);
        } catch (NotFoundException e) {
            httpResponse.found(Headers.builder()
                            .location("/401.html"),
                    MessageBody.emptyBody());
        }
    }

    private void login(final User user, final String password, final HttpResponse httpResponse) {
        if (user.checkPassword(password)) {
            log.info("User Login : {}", user);
            Session session = Session.newSession();
            session.setAttribute("user", user);
            SessionManager.add(session);
            httpResponse.found(
                    Headers.builder()
                            .setCookie(session.getId())
                            .location("/index.html"),
                    MessageBody.emptyBody());
            return;
        }
        httpResponse.found(Headers.builder()
                        .location("/401.html"),
                MessageBody.emptyBody());
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        HttpCookie cookie = httpRequest.getHeaders().getCookie();
        String jsessionid = cookie.getCookie("JSESSIONID");
        if (cookie.containsJSESSIONID() && SessionManager.contains(jsessionid)) {
            redirectResponse(cookie, httpResponse);
            return;
        }
        String uri = httpRequest.getUri();
        String responseBody = FileReader.read(uri + ".html");
        httpResponse.ok(ContentType.from(uri), new MessageBody(responseBody));
    }

    private void redirectResponse(final HttpCookie cookie, final HttpResponse httpResponse) {
        String jsessionid = cookie.getCookie("JSESSIONID");
        Session session = SessionManager.findSession(jsessionid);
        User user = (User) session.getAttribute("user");
        log.info("Login User : {}", user);
        httpResponse.found(Headers.builder()
                        .location("/index.html"),
                MessageBody.emptyBody());
    }
}

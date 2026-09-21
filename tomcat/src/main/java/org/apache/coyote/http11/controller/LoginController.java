package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.ReasonPhrase;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.apache.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER = "user";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final Map<String, String> queryParams = request.getParameters();
        HttpCookie httpCookie = request.getCookies();

        final String account = queryParams.get("account");
        final String password = queryParams.get("password");

        login(account, password, httpCookie, response);
    }

    private void login(String account, String password, HttpCookie httpCookie, HttpResponse response)
            throws IOException {
        if (account != null && password != null) {
            final Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);

            if (foundUser.isEmpty()) {
                redirectToUnauthorized(response);
                return;
            }

            final User user = foundUser.get();

            if (!user.checkPassword(password)) {
                redirectToUnauthorized(response);
                return;
            }

            log.info("로그인 성공! 아이디 : {}", user.getAccount());

            removeSessionIfExists(httpCookie);

            final Session session = createSession(user);

            response.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302, new ReasonPhrase("Found"));
            response.setSession(session);
            response.setLocation("/index.html");
            response.write();
            return;
        }
        redirectToUnauthorized(response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpCookie httpCookie = request.getCookies();

        if (httpCookie.get(JSESSIONID) != null) {
            final HttpSession session = SessionManager.getInstance().findSession(httpCookie.get(JSESSIONID));

            if (session != null) {
                response.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302,
                        new ReasonPhrase("Found"));

                response.setLocation("/index.html");
                response.write();
                return;
            }
        }

        final String body = new FileReader().readFile("static/login.html");

        response.setContentType(ContentType.TEXT_HTML);
        response.setHttpBody(new HttpBody(body));

        response.write();
    }

    private void redirectToUnauthorized(HttpResponse response) throws IOException {
        response.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302, new ReasonPhrase("Found"));
        response.setLocation("/401.html");
        response.write();
    }

    private Session createSession(User user) {
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(USER, user);
        SessionManager.getInstance().add(session);
        return session;
    }

    private void removeSessionIfExists(HttpCookie httpCookie) {
        if (httpCookie.get(JSESSIONID) != null) {
            HttpSession existedSession = SessionManager.getInstance().findSession(httpCookie.get(JSESSIONID));
            if (existedSession != null) {
                SessionManager.getInstance().remove(existedSession);
            }
        }
    }
}

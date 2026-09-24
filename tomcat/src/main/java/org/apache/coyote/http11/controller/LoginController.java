package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.Cookie;
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
                response.sendRedirect("/401.html");
                return;
            }

            final User user = foundUser.get();

            if (!user.checkPassword(password)) {
                response.sendRedirect("/401.html");
                return;
            }

            log.info("로그인 성공! 아이디 : {}", user.getAccount());

            removeSessionIfExists(httpCookie);

            final Session session = createSession(user);

            response.setCookie(new Cookie(JSESSIONID, session.getId()));
            response.sendRedirect("/index.html");
            return;
        }
        response.sendRedirect("/401.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpCookie httpCookie = request.getCookies();

        if (httpCookie.get(JSESSIONID) != null) {
            final HttpSession session = SessionManager.getInstance().findSession(httpCookie.get(JSESSIONID));

            if (session != null) {
                response.sendRedirect("/index.html");
                return;
            }
        }

        final String body = new FileReader().readFile("static/login.html");

        response.setContentType(ContentType.TEXT_HTML);
        response.setHttpBody(new HttpBody(body));
        response.send();
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

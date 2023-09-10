package nextstep.jwp.controller;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.handler.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SessionManager sessionManager = new SessionManager();

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) {
        final String sessionId = request.getCookie(Session.SESSION_KEY);
        final Session session = sessionManager.findSession(sessionId);
        if (session == null) {
            response.setStatusCode(HttpStatusCode.OK);
            final String responseBody = ViewResolver.read("/login.html");
            response.setResponseBody(responseBody);
            return;
        }
        response.setStatusCode(HttpStatusCode.FOUND);
        response.setHeader("Location", "/");
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> requestBody = request.getRequestBody();
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        final Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
        final boolean isSuccess = findUser.filter(user -> user.checkPassword(password))
                .isPresent();
        if (isSuccess) {
            final Session session = new Session();
            sessionManager.add(session);
            session.setAttribute(session.getId(), findUser.get());
            response.setStatusCode(HttpStatusCode.FOUND);
            response.setHeader("Location", "/");
            response.setHeader("Set-Cookie", Session.SESSION_KEY + "=" + session.getId());
            log.info("로그인 성공! 로그인 아이디: " + account);
            return;
        }
        response.setStatusCode(HttpStatusCode.FOUND);
        response.setHeader("Location", "/401.html");
    }
}

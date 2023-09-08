package nextstep.jwp.controller;

import handler.Controller;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final SessionManager sessionManager = new SessionManager();

    @Override
    public String run(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final HttpMethod method = httpRequest.getMethod();
        if (method.equals(HttpMethod.GET)) {
            final String sessionId = httpRequest.getCookie(Session.SESSION_KEY);
            final Session session = sessionManager.findSession(sessionId);
            if (session == null) {
                httpResponse.setStatusCode(HttpStatusCode.OK);
                return "/login.html";
            }
            httpResponse.setStatusCode(HttpStatusCode.FOUND);
            httpResponse.setHeader("Location", "/");

            return "/index.html";
        }
        final Map<String, String> requestBody = httpRequest.getRequestBody();
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        final Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
        final boolean isSuccess = findUser.filter(user -> user.checkPassword(password))
                .isPresent();
        if (isSuccess) {
            final Session session = new Session();
            sessionManager.add(session);
            session.setAttribute(session.getId(), findUser.get());
            httpResponse.setStatusCode(HttpStatusCode.FOUND);
            httpResponse.setHeader("Location", "/");
            httpResponse.setHeader("Set-Cookie", Session.SESSION_KEY + "=" + session.getId());
            log.info("로그인 성공! 로그인 아이디: " + account);
            return "/index.html";
        }
        httpResponse.setStatusCode(HttpStatusCode.FOUND);
        httpResponse.setHeader("Location", "/401.html");
        return "/401.html";
    }
}

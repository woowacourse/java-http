package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.Account;
import com.techcourse.model.Password;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.catalina.resolver.StaticResourceResolver;
import org.apache.catalina.resolver.StaticResourceResolver.ResolvedResource;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final String COOKIE = "JSESSIONID=";
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final StaticResourceResolver resolver = new StaticResourceResolver();

    @Override
    public Http11Response doGet(final Http11Request request) {
        String sessionId = request.getSessionId();
        Session session = SessionManager.findSession(sessionId);
        if (session != null && session.getAttribute("user") != null) {
            return Http11Response.redirect("/index.html", COOKIE + session.getId());
        }
        try {
            String uri = request.getUri();
            ResolvedResource resource = resolver.resolve(uri);
            String body = Files.readString(Paths.get(resource.url().toURI()));

            return Http11Response.ok("text/html;charset=utf-8", body);
        } catch (IOException | URISyntaxException e) {
            log.error("로그인 페이지 읽기 실패", e);
            return Http11Response.serverError();
        }
    }

    @Override
    public Http11Response doPost(final Http11Request request) {
        final Map<String, String> params = request.extractRequestBodyParams();
        final Account account = new Account(params.get("account"));
        final Password password = new Password(params.get("password"));

        final var loginSuccess = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (loginSuccess.isPresent()) {
            log.info("로그인 성공 - {}", account);
            String sessionId = request.getSessionId();
            if (sessionId == null) {
                //로그인을 성공하고, 세션이 없는 상황 -> 즉 첫 로그인 요청인 상황임
                Session session = SessionManager.create(true);
                session.setAttribute("user", loginSuccess.get());

                return Http11Response.redirect("/index.html", COOKIE + session.getId());
            }
            Session oldSession = SessionManager.findSession(sessionId);
            oldSession.invalidate();
            SessionManager.remove(sessionId);

            Session newSession = SessionManager.create(true);
            newSession.setAttribute("user", loginSuccess.get());
            String cookieHeader = COOKIE + newSession.getId();
            return Http11Response.redirect("/index.html", cookieHeader);
        }
        log.warn("로그인 실패 - {}", account);
        return Http11Response.redirect("/401.html");
    }
}

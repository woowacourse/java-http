package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public boolean supports(final HttpRequest request) {
        return request.getRequestLine().getPath().equals("/login");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final String account = request.getRequestParams().get("account");
        final String password = request.getRequestParams().get("password");
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user.get());

            log.atInfo().log("로그인 성공. user: {}", user.get());
            log.atInfo().log("세션 생성. JSESSIONID: {}", session.getId());
            SessionManager.getInstance().add(session);

            final byte[] body = "로그인 성공".getBytes(StandardCharsets.UTF_8);
            response.putHeader("Location", "/index.html");
            response.putHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; Max-Age=3600");

            response.setStatusLine("HTTP/1.1 302 Found");
            response.setBody(body);
        } else {
            log.atInfo().log("로그인 실패");
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            final byte[] body = Files.readAllBytes(Path.of(resource.toURI()));

            response.setStatusLine("HTTP/1.1 401 Unauthorized");
            response.setBody(body);
        }
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String sessionId = request.getHttpCookies().get("JSESSIONID");

        if (sessionId != null && SessionManager.getInstance().findSession(sessionId) != null) {
            final byte[] body = new byte[0];
            response.putHeader("Location", "/index.html");

            response.setStatusLine("HTTP/1.1 302 Found");
            response.setBody(body);
        } else {
            if (sessionId != null) {
                log.atInfo().log("만료되었거나 유효하지 않은 세션입니다. JSESSIONID: {}", sessionId);
                response.putHeader("Set-Cookie", "JSESSIONID=; Max-Age=0;");
            }
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            if (resource != null) {
                final byte[] body = Files.readAllBytes(Path.of(resource.toURI()));

                response.setStatusLine("HTTP/1.1 200 OK");
                response.setBody(body);
            }
        }
    }
}

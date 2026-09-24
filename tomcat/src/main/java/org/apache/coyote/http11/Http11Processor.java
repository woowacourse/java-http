package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequest.from(inputStream);
            HttpResponse response = new HttpResponse(outputStream);

            final Optional<String> requestedSessionId = request.getCookies().getValue("JSESSIONID");
            final Session session = request.getSession(true);
            if (requestedSessionId.filter(session.getId()::equals).isEmpty()) {
                response.addCookie("JSESSIONID", session.getId());
            }

            if (request.getMethod().equals("POST") && request.getPath().equals("/register")) {
                register(request.getBodyParams());
                response.sendRedirect("/index.html");
                return;
            }

            if (request.getMethod().equals("POST") && request.getPath().equals("/login")) {
                final boolean loginSucceed = login(request);
                final String location = loginSucceed ? "/index.html" : "/401.html";
                response.sendRedirect(location);
                return;
            }

            if (request.getMethod().equals("GET") && request.getPath().equals("/login")
                    && isLoggedIn(request)) {
                response.sendRedirect("/index.html");
                return;
            }

            String path = request.getPath();
            if (path.equals("/")) {
                response.setStatus("200 OK");
                response.addHeader("Content-Type", "text/html;charset=utf-8");
                response.setBody("Hello world!".getBytes(StandardCharsets.UTF_8));
                response.send();
                return;
            }

            if (path.equals("/login") || path.equals("/register")) {
                path += ".html";
            }
            response.forward(path);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void register(final Map<String, String> params) {
        final User user = new User(
                params.get("account"),
                params.get("password"),
                params.get("email")
        );
        InMemoryUserRepository.save(user);
    }

    private boolean login(final HttpRequest request) {
        final Map<String, String> params = request.getBodyParams();
        final Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"));
        if (user.isEmpty()) {
            log.info("존재하지 않는 계정입니다.");
            return false;
        }

        final User foundUser = user.get();
        if (!foundUser.checkPassword(params.get("password"))) {
            log.info("비밀번호가 일치하지 않습니다.");
            return false;
        }

        log.info("로그인 성공! 아이디 : {}", foundUser.getAccount());
        request.getSession(true).setAttribute("user", foundUser);
        return true;
    }

    private boolean isLoggedIn(final HttpRequest request) {
        final Session session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

}

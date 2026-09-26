package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER_ATTRIBUTE = "user";

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
            final HttpRequest request = HttpRequest.from(inputStream);
            final HttpResponse response = new HttpResponse();

            log.debug("{} {} 요청을 받았습니다.", request.getMethod(), request.getPath());

            handle(request, response);
            response.write(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(final HttpRequest request, final HttpResponse response) throws IOException {
        final HttpMethod method = request.getMethod();
        final String path = request.getPath();

        if (method == HttpMethod.POST && path.equals("/login")) {
            login(request, response);
            return;
        }

        if (method == HttpMethod.POST && path.equals("/register")) {
            register(request, response);
            return;
        }

        if (path.equals("/")) {
            response.setBody("text/html;charset=utf-8", "Hello world!");
            return;
        }

        if (path.equals("/login")) {
            showLoginPage(request, response);
            return;
        }

        if (path.equals("/register")) {
            response.sendStaticResource("/register.html");
            return;
        }

        response.sendStaticResource(path);
    }

    private void showLoginPage(final HttpRequest request, final HttpResponse response) throws IOException {
        final Cookie cookie = request.getCookie();
        if (isLoggedIn(cookie)) {
            log.info("이미 로그인된 사용자입니다. index.html로 이동합니다.");
            response.sendRedirect(INDEX_PAGE);
            return;
        }

        if (!cookie.hasJSessionId()) {
            response.addCookie(JSESSIONID, createSession().getId());
        }
        response.sendStaticResource("/login.html");
    }

    private void register(final HttpRequest request, final HttpResponse response) {
        final String account = request.getBodyParam("account");
        final String email = request.getBodyParam("email");
        final String password = request.getBodyParam("password");

        if (isBlank(account) || isBlank(email) || isBlank(password)) {
            log.info("회원가입에 필요한 정보가 입력되지 않았습니다.");
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            log.info("이미 존재하는 계정입니다. account: {}", account);
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        log.info("회원가입이 완료되었습니다. account: {}", account);
        response.sendRedirect(INDEX_PAGE);
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    private boolean isLoggedIn(final Cookie cookie) {
        final Session session = SessionManager.getInstance().findSession(cookie.getJSessionId());
        return session != null && session.getAttribute(USER_ATTRIBUTE) != null;
    }

    private Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        SessionManager.getInstance().add(session);
        return session;
    }

    private void login(final HttpRequest request, final HttpResponse response) {
        final String account = request.getBodyParam("account");
        final String password = request.getBodyParam("password");

        if (account == null || password == null) {
            log.info("아이디 또는 비밀번호가 입력되지 않았습니다.");
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));

        if (user.isEmpty()) {
            log.info("아이디 또는 비밀번호가 일치하지 않습니다. account: {}", account);
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        log.info("로그인 성공! 아이디 : {}", account);

        final Optional<Session> existingSession = findSession(request.getCookie());
        final Session session = existingSession.orElseGet(this::createSession);
        session.setAttribute(USER_ATTRIBUTE, user.get());

        if (existingSession.isEmpty()) {
            response.addCookie(JSESSIONID, session.getId());
        }
        response.sendRedirect(INDEX_PAGE);
    }

    private Optional<Session> findSession(final Cookie cookie) {
        if (!cookie.hasJSessionId()) {
            return Optional.empty();
        }
        return Optional.ofNullable(SessionManager.getInstance().findSession(cookie.getJSessionId()));
    }
}

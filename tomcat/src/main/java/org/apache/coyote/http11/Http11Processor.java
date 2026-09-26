package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
            final HttpMethod method = request.getMethod();
            final String path = request.getPath();
            final Cookie cookie = request.getCookie();

            log.debug("{} {} 요청을 받았습니다.", method, path);

            if (method == HttpMethod.POST && path.equals("/login")) {
                login(request, outputStream);
                return;
            }

            if (method == HttpMethod.POST && path.equals("/register")) {
                register(request, outputStream);
                return;
            }

            if (path.equals("/")) {
                final var responseBody = "Hello world!";
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            String filePath = "static" + path;
            String contentType = "text/html;charset=utf-8";

            if (path.equals("/login")) {
                if (isLoggedIn(cookie)) {
                    log.info("이미 로그인된 사용자입니다. index.html로 이동합니다.");
                    sendRedirect(outputStream, INDEX_PAGE, null);
                    return;
                }
                filePath = "static/login.html";
            }

            if (path.equals("/register")) {
                filePath = "static/register.html";
            }

            if (path.endsWith(".css")) {
                contentType = "text/css;charset=utf-8";
            }

            if (path.endsWith(".js")) {
                contentType = "application/javascript;charset=utf-8";
            }


            final URL resource = getClass().getClassLoader().getResource(filePath);
            if (resource == null) {
                final var response404 = "HTTP/1.1 404 Not Found\r\n\r\n";
                outputStream.write(response404.getBytes());
                outputStream.flush();
                return;
            }

            final byte[] body = Files.readAllBytes(Path.of(resource.toURI()));

            final StringBuilder responseHeader = new StringBuilder()
                    .append("HTTP/1.1 200 OK ").append("\r\n")
                    .append("Content-Type: ").append(contentType).append(" ").append("\r\n")
                    .append("Content-Length: ").append(body.length).append(" ").append("\r\n");

            if (path.equals("/login") && !cookie.hasJSessionId()) {
                final Session session = createSession();
                responseHeader.append("Set-Cookie: ").append(JSESSIONID).append("=").append(session.getId())
                        .append(" ").append("\r\n");
            }
            responseHeader.append("\r\n");

            outputStream.write(responseHeader.toString().getBytes());
            outputStream.write(body);
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(final HttpRequest request, final OutputStream outputStream) throws IOException {
        final String account = request.getBodyParam("account");
        final String email = request.getBodyParam("email");
        final String password = request.getBodyParam("password");

        if (isBlank(account) || isBlank(email) || isBlank(password)) {
            log.info("회원가입에 필요한 정보가 입력되지 않았습니다.");
            sendRedirect(outputStream, UNAUTHORIZED_PAGE, null);
            return;
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            log.info("이미 존재하는 계정입니다. account: {}", account);
            sendRedirect(outputStream, UNAUTHORIZED_PAGE, null);
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        log.info("회원가입이 완료되었습니다. account: {}", account);
        sendRedirect(outputStream, INDEX_PAGE, null);
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    private void sendRedirect(final OutputStream outputStream, final String location, final String sessionId)
            throws IOException {
        final StringBuilder response = new StringBuilder()
                .append("HTTP/1.1 302 Found ").append("\r\n")
                .append("Location: ").append(location).append(" ").append("\r\n");

        if (sessionId != null) {
            response.append("Set-Cookie: ").append(JSESSIONID).append("=").append(sessionId).append(" ").append("\r\n");
        }
        response.append("\r\n");

        outputStream.write(response.toString().getBytes());
        outputStream.flush();
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

    private void login(final HttpRequest request, final OutputStream outputStream) throws IOException {
        final String account = request.getBodyParam("account");
        final String password = request.getBodyParam("password");

        if (account == null || password == null) {
            log.info("아이디 또는 비밀번호가 입력되지 않았습니다.");
            sendRedirect(outputStream, UNAUTHORIZED_PAGE, null);
            return;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));

        if (user.isEmpty()) {
            log.info("아이디 또는 비밀번호가 일치하지 않습니다. account: {}", account);
            sendRedirect(outputStream, UNAUTHORIZED_PAGE, null);
            return;
        }

        log.info("로그인 성공! 아이디 : {}", account);

        final Optional<Session> existingSession = findSession(request.getCookie());
        final Session session = existingSession.orElseGet(this::createSession);
        session.setAttribute(USER_ATTRIBUTE, user.get());

        final String sessionIdToSend = existingSession.isPresent() ? null : session.getId();
        sendRedirect(outputStream, INDEX_PAGE, sessionIdToSend);
    }

    private Optional<Session> findSession(final Cookie cookie) {
        if (!cookie.hasJSessionId()) {
            return Optional.empty();
        }
        return Optional.ofNullable(SessionManager.getInstance().findSession(cookie.getJSessionId()));
    }
}

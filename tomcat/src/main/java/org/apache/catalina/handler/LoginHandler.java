package org.apache.catalina.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.ServletException;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginHandler extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_FILE_LOCATION = "static";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void service(final Http11Request request, final Http11Response response) throws Exception {
        super.service(request, response);
    }

    @Override
    void doGet(final Http11Request request, final Http11Response response) throws Exception {
        final Optional<String> sessionId = request.findCookie("JSESSIONID");

        if (sessionId.isPresent()) {
            final Session session = sessionManager.findSession(sessionId.get());
            if (session != null) {
                response.setRedirectResponse("/index.html");
                return;
            }
        }

        final byte[] fileContent = readFile("/login.html");

        response.setResponse(HttpStatus.OK, fileContent, HTML_CONTENT_TYPE);
    }

    @Override
    void doPost(final Http11Request request, final Http11Response response) throws Exception {
        final Map<String, String> body = request.getBodyByContentType(HttpContentType.URL);

        final String account = body.get("account");
        final String password = body.get("password");

        final User user = findUser(account, password);
        final Session session = createSession(user);

        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Set-Cookie", String.format("JSESSIONID=%s; Path=/; HttpOnly; SameSite=Strict", session.getId()));

        response.setRedirectResponse("/index.html", headers);
    }

    private byte[] readFile(final String location) throws IOException {
        try (final InputStream fileInputStream = new FileInputStream(getClass().getClassLoader().getResource(STATIC_FILE_LOCATION + location).getPath())) {
            return fileInputStream.readAllBytes();
        } catch (final NullPointerException e) {
            throw new NoSuchFileException(location);
        }
    }

    private User findUser(final String account, final String password) throws ServletException {
        final Optional<User> userOrEmpty = findUserByAccount(account, password);
        return userOrEmpty.orElseThrow(() -> new ServletException("login failed"));
    }

    private Optional<User> findUserByAccount(final String account, final String password) {
        final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(account);

        if (userOrEmpty.isEmpty()) {
            log.warn("User not found : account = {}", account);
            return Optional.empty();
        }

        final User user = userOrEmpty.get();
        if (!user.checkPassword(password)) {
            log.warn("Wrong password : account = {}", account);
            return Optional.empty();
        }

        log.info("User found : {}", user);
        return Optional.of(user);
    }

    private Session createSession(final User user) {
        final String sessionId = generateSessionID();

        final Session session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);

        return session;
    }

    private String generateSessionID() {
        final UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}

package org.apache.coyote.http11.httpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.httpRequest.HttpCookie;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseBody {

    private static final Logger log = LoggerFactory.getLogger(ResponseBody.class);
    private static final String DEFAULT_RESOURCE_PATH = "static";

    private final HttpRequest httpRequest;

    public ResponseBody(
            final HttpRequest httpRequest
    ) {
        this.httpRequest = httpRequest;
    }

    public ResponseContent getContent() throws IOException {
        final String path = httpRequest.getPath();
        if (path.equals("/")) {
            return getDefaultResponseContent();
        }

        if ("/login".equals(path)) {
            return getLoginResponseContent();
        }

        if ("/register".equals(path)) {
            return getRegisterResponseContent();
        }

        String filePath = DEFAULT_RESOURCE_PATH + path;
        if (!path.contains(".")) {
            filePath += ".html";
        }

        return createHttpResponseContentFrom(filePath);
    }

    private ResponseContent getDefaultResponseContent() {
        return ResponseContent.success("Hello world!", null);
    }

    private ResponseContent getLoginResponseContent() throws IOException {
        try {
            final Optional<String> cookieOfRequest = httpRequest.findCookie();
            Session session;
            HttpCookie httpCookie;

            if (cookieOfRequest.isPresent()) {
                httpCookie = HttpCookie.parse(cookieOfRequest.get());
                final String sessionId = httpCookie.getCookies().get("JSESSIONID");
                if (sessionId != null) {
                    session = SessionManager.findSession(sessionId);

                    final User user = (User) session.getAttribute("user");
                    if (user != null) {
                        final String body = getBodyFromStaticFile("/index.html");
                        return ResponseContent.redirect(body, "/index.html", httpCookie);
                    }
                }
            }

            final String account = findValueFromParams("account");
            final String password = findValueFromParams("password");

            final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(account);
            if (userOrEmpty.isPresent()) {
                final User user = userOrEmpty.get();
                log.info("user: {}", user);

                if (!user.checkPassword(password)) {
                    final String body = getBodyFromStaticFile("/401.html");
                    return ResponseContent.redirect(body, "/401.html", null);
                }

                session = Session.create();
                session.setAttribute("user", user);
                SessionManager.add(session);
                httpCookie = HttpCookie.create(session.getSessionId());

                final String body = getBodyFromStaticFile("/index.html");
                return ResponseContent.redirect(body, "/index.html", httpCookie);
            }

            final String body = getBodyFromStaticFile("/login.html");
            return ResponseContent.redirect(body, "/login.html", null);
        } catch (IllegalArgumentException e) {
            final String body = getBodyFromStaticFile("/login.html");
            return ResponseContent.redirect(body, "/login.html", null);
        }
    }

    private ResponseContent getRegisterResponseContent() throws IOException {
        try {
            final String account = findValueFromParams("account");
            final String password = findValueFromParams("password");
            final String email = findValueFromParams("email");

            final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(account);
            if (userOrEmpty.isPresent()) {
                log.warn("id: {}", account);
                throw new IllegalArgumentException("이미 가입된 계정입니다.");
            }

            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            final String body = getBodyFromStaticFile("/index.html");
            return ResponseContent.redirect(body, "/index.html", null);
        } catch (IllegalArgumentException e) {
            final String body = getBodyFromStaticFile("/register.html");
            return ResponseContent.redirect(body, "/register.html", null);
        }
    }

    private String getBodyFromStaticFile(final String fileName) throws IOException {
        final String filePath = DEFAULT_RESOURCE_PATH + fileName;
        final URL resource = getClass().getClassLoader().getResource(filePath);
        return getBodyFromResource(resource);
    }

    private String getBodyFromResource(final URL resource) throws IOException {
        final File file = new File(resource.getFile());
        final Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }

    private ResponseContent createHttpResponseContentFrom(
            String filePath
    ) throws IOException {
        URL resource  = getClass().getClassLoader().getResource(filePath);
        if (resource == null) {
            final String body = getBodyFromStaticFile("/404.html");
            return ResponseContent.error(body);
        }

        final String body = getBodyFromResource(resource);
        return ResponseContent.success(body, null);
    }

    private String findValueFromParams(final String name) {
        return httpRequest.findParamsValueFromBody(name)
                .orElseThrow(() -> new IllegalArgumentException("파라미터의 키 값이 존재하지 않습니다: " + name));
    }
}

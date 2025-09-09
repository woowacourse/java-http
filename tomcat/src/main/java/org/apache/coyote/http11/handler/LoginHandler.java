package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.message.HttpCookie;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.StatusLine;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private final SessionManager sessionManager = new SessionManager();

    @Override
    public boolean canHandle(final HttpRequest request) {
        return "/login".equals(request.getPath());
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        final String httpVersion = request.getVersion();

        if (request.equalMethod("GET") && request.hasSessionCookie()) {
            Session session = sessionManager.findSession(request.getJsessionId());
            if (session != null && session.getAttribute("user") != null) {
                return redirectResponse(httpVersion, "/index.html");
            }
        }

        final Map<String, String> params;
        if (request.equalMethod("POST")) {
            params = request.getFormParams();
        } else {
            params = request.getQueryParams();
        }

        final String account = params.get("account");
        final String password = params.get("password");

        if (account == null || password == null) {
            return loginPageResponse(httpVersion, "/login.html");
        }

        final Optional<User> authenticatedUser = authenticate(account, password);

        if (authenticatedUser.isPresent()) {
            final User user = authenticatedUser.get();
            log.info("user : {}", user);

            if (request.hasSessionCookie()) {
                final Session session = sessionManager.findSession(request.getJsessionId());

                if (session != null && session.getAttribute("user") != null) {
                    return redirectResponse(httpVersion, "/index.html");
                }
            }

            return loginRedirectResponse(httpVersion, "/index.html", user);
        }

        return redirectResponse(httpVersion, "/401.html");
    }

    private Optional<User> authenticate(final String account, final String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private HttpResponse loginPageResponse(final String httpVersion, final String location) throws IOException {
        final String resourcePath = "static" + location;
        final URL resource = getClass().getClassLoader().getResource(resourcePath);

        String body = "";
        if (resource != null) {
            body = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.HTML.getMimeType());
        headers.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        return new HttpResponse(
                new StatusLine(httpVersion, StatusCode.OK),
                headers,
                body.getBytes(StandardCharsets.UTF_8)
        );
    }

    private HttpResponse loginRedirectResponse(final String httpVersion, final String location, final User user) {
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("location", location);
        headers.addHeader("Content-Length", "0");

        addSessionCookie(headers, user);

        return new HttpResponse(
                new StatusLine(httpVersion, StatusCode.FOUND),
                headers,
                new byte[0]
        );
    }

    private HttpResponse redirectResponse(final String httpVersion, final String location) {
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("location", location);
        headers.addHeader("Content-Length", "0");

        return new HttpResponse(
                new StatusLine(httpVersion, StatusCode.FOUND),
                headers,
                new byte[0]
        );
    }

    private void addSessionCookie(final HttpHeaders headers, final User user) {
        final Session session = new Session();
        session.setAttribute("user", user);
        sessionManager.add(session);

        final HttpCookie cookie = HttpCookie.of("JSESSIONID", session.getId());
        headers.addHeader("Set-Cookie", cookie.toHeaderCookie());
    }
}

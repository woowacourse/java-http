package org.apache.catalina.servlet;

import static org.apache.catalina.StaticResourceUtils.getContentType;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.HttpSession;
import org.apache.catalina.HttpSessionManager;
import org.apache.catalina.StaticResourceUtils;
import org.apache.coyote.util.Cookie;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends AbstractServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    private final HttpSessionManager httpSessionManager;

    public LoginServlet(final HttpSessionManager httpSessionManager) {
        this.httpSessionManager = httpSessionManager;
    }

    @Override
    public boolean possibleHandle(final String requestPath) {
        return Objects.equals(requestPath, "/login");
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) {
        String httpVersion = request.getHttpVersion();
        String responseBody = StaticResourceUtils.readResourceContent(request.getRequestPath());
        int statusCode = 200;
        String statusMessage = "OK";
        if (validSession(request.getCookie("JSESSIONID"))) {
            log.info("리다이렉트");
            statusCode = 302;
            statusMessage = "Found";
            responseBody = "";
            response.putHeader("Location", "/index.html");
        }
        response.putHeader("Content-Type", StaticResourceUtils.getContentType(request.getRequestPath()) + ";charset=utf-8");
        response.putHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        response.update(httpVersion, statusCode, statusMessage, responseBody);
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
        String httpVersion = request.getHttpVersion();
        int statusCode = 401;
        String statusMessage = "Unauthorized";
        String responseBody = StaticResourceUtils.readResourceContent("/401.html");

        Optional<User> user = InMemoryUserRepository.findByAccount(request.getRequestValue("account"));
        if (user.isPresent() && user.get().checkPassword(request.getRequestValue("password"))) {
            log.info(user.toString());
            statusCode = 302;
            statusMessage = "Found";
            responseBody = "";
            UUID jSessionId = UUID.randomUUID();
            httpSessionManager.add(new HttpSession(jSessionId, user.get()));
            response.putHeader("Set-Cookie", "JSESSIONID=" + jSessionId);
            response.putHeader("Location", "/index.html");
        }
        response.putHeader("Content-Type", getContentType(request.getRequestPath()) + ";charset=utf-8");
        response.putHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        response.update(httpVersion, statusCode, statusMessage, responseBody);
    }

    private boolean validSession(final Cookie jSessionCookie) {
        if (jSessionCookie == null) {
            return false;
        }
        String jSessionId = jSessionCookie.value();
        return httpSessionManager.find(UUID.fromString(jSessionId)).isPresent();
    }
}

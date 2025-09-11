package org.apache.catalina.controller.auth;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.net.HttpCookie;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.resource.StaticResourceController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final Manager manager;
    private final StaticResourceController staticResourceController;

    public LoginController(final StaticResourceController staticResourceController) {
        this.manager = SessionManager.getInstance();
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected void doGet(
            final Http11Request request,
            final Http11Response response
    ) throws Exception {
        final var sessionOptional = getSession(request, false);
        if (sessionOptional.isPresent() && sessionOptional.get().getAttribute("user") != null) {
            response.setStatus(302);
            response.setHeader("Location", "/index.html");
            return;
        }
        try {
            staticResourceController.service(request, response);
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
    }

    @Override
    protected void doPost(
            final Http11Request httpRequest,
            final Http11Response httpResponse
    ) {
        final var params = extractFirstParamValues(RequestLine.parseUrlEncodedParams(httpRequest.getBody()));
        final var userOptional = isLoginSuccessful(params);
        if (userOptional.isPresent()) {
            final var user = userOptional.get();
            final var session = getSession(httpRequest, true)
                    .orElseThrow(() -> new IllegalStateException("세션 생성에 실패했습니다."));
            session.setAttribute("user", user);
            httpResponse.setStatus(302);
            httpResponse.setHeader("Location", "/index.html");
            final String cookieValue = String.format("%s=%s; Path=/; HttpOnly; SameSite=Lax", "JSESSIONID",
                    session.getId());
            httpResponse.addHeader("Set-Cookie", cookieValue);
            return;
        }
        httpResponse.setStatus(302);
        httpResponse.setHeader("Location", "/401.html");
    }

    private Optional<Session> getSession(
            final Http11Request request,
            final boolean create
    ) {
        final var jSessionIdOptional = request.getCookies()
                .getCookie("JSESSIONID")
                .map(HttpCookie::getValue);
        return jSessionIdOptional.flatMap(manager::findSession)
                .or(() -> {
                    if (create) {
                        return Optional.of(createNewSession());
                    }
                    return Optional.empty();
                });
    }

    private Session createNewSession() {
        final var newSession = new Session(UUID.randomUUID().toString());
        manager.add(newSession);
        return newSession;
    }

    private Optional<User> isLoginSuccessful(final Map<String, String> params) {
        if (!params.containsKey("account") || !params.containsKey("password")) {
            return Optional.empty();
        }
        final String account = params.get("account");
        final String password = params.get("password");
        final var userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        final var user = userOptional.get();
        if (!user.checkPassword(password)) {
            return Optional.empty();
        }
        log.info("login success: {}", user);
        return Optional.of(user);
    }
}

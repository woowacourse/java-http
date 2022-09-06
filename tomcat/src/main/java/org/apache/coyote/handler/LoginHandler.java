package org.apache.coyote.handler;

import static org.apache.coyote.response.ContentType.HTML;
import static org.apache.coyote.response.StatusCode.FOUND;
import static org.apache.coyote.response.StatusCode.OK;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.QueryParams;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.Location;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private LoginHandler() {
    }

    public static HttpResponse loginWithGet(HttpRequest httpRequest) {
        final Optional<Cookie> optionalCookie = httpRequest.getJSessionCookie();
        if (optionalCookie.isPresent()) {
            final Cookie cookie = optionalCookie.get();
            handleSession(cookie);
            return HttpResponse.of(FOUND, HTML, Location.from("/index.html"));
        }

        return HttpResponse.of(OK, HTML, "/login.html");
    }

    public static HttpResponse login(final HttpRequest request) {
        final QueryParams queryParams = QueryParams.from(request.getRequestBody());
        final String account = queryParams.getValueFromKey("account");
        final String password = queryParams.getValueFromKey("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchUserException::new);

        if (user.checkPassword(password)) {
            final String userInformation = user.toString();
            log.info(userInformation);
            final Session session = saveUserInSession(user);

            return makeLoginSuccessResponse(request, session);
        }

        return HttpResponse.of(FOUND, HTML, Location.from("/401.html"));
    }

    private static void handleSession(Cookie cookie) {
        final Optional<Session> session = SessionManager.findSession(cookie.getValue());
        if (session.isPresent()) {
            final User user = (User) session.get().getAttribute("user");
            log.info("User : {}", user);
        }
    }

    private static Session saveUserInSession(User user) {
        final Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);

        return session;
    }

    private static HttpResponse makeLoginSuccessResponse(HttpRequest request, Session session) {
        final Optional<Cookie> cookie = request.getJSessionCookie();
        if (cookie.isEmpty()) {
            final Cookie jsessionid = Cookie.ofJSessionId(session.getId());
            return HttpResponse.of(FOUND, HTML, Location.from("/index.html"), jsessionid);
        }

        return HttpResponse.of(FOUND, HTML, Location.from("/index.html"));
    }
}

package org.apache.coyote.http11.auth;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.response.ResponsePage.INDEX_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.LOGIN_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.UNAUTHORIZED_PAGE;

public class LoginService {

    public static final String COOKIE_KEY = "JSESSIONID";

    private final SessionRepository sessionRepository = new SessionRepository();

    public HttpResponse getLoginViewResponse(Cookie cookie, Protocol protocol) {
        Optional<String> cookieOption = cookie.get(COOKIE_KEY);
        if (cookieOption.isEmpty()) {
            return HttpResponse.getCookieNullResponseEntity(protocol, LOGIN_PAGE);
        }
        final Optional<Session> session = sessionRepository.getSession(cookieOption.get());
        if (session.isEmpty()) {
            return HttpResponse.getCookieNullResponseEntity(protocol, LOGIN_PAGE);
        }
        return HttpResponse.getCookieNullResponseEntity(protocol, INDEX_PAGE);
    }

    public HttpResponse getLoginOrElseUnAuthorizedResponse(Protocol protocol, String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> getSuccessLoginResponse(user, protocol))
                .orElseGet(() -> HttpResponse.getCookieNullResponseEntity(protocol, UNAUTHORIZED_PAGE));
    }

    private HttpResponse getSuccessLoginResponse(final User user, final Protocol protocol) {
        final String uuid = UUID.randomUUID().toString();
        final Session session = Session.from(uuid);
        session.setAttribute("user", user);
        sessionRepository.create(session);
        Cookie cookie = new Cookie();
        cookie.setSession(session);
        return HttpResponse.getResponseEntity(protocol, cookie, INDEX_PAGE);
    }

}

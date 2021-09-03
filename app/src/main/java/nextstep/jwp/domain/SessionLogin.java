package nextstep.jwp.domain;

import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.http.request.CookieType;
import nextstep.jwp.http.request.HttpCookies;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

public class SessionLogin {

    private final HttpCookies httpCookies;

    public SessionLogin(HttpCookies httpCookies) {
        this.httpCookies = httpCookies;
    }

    public boolean isSuccess() {
        final Optional<String> valueFromKey = httpCookies.valueFromKey(CookieType.JSESSIONID.value());
        if (valueFromKey.isEmpty()) {
            return false;
        }
        final HttpSession httpSession = HttpSessions.getSession(valueFromKey.get());
        return !Objects.isNull(httpSession);
    }
}

package nextstep.jwp.domain;

import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.http.request.CookieType;
import nextstep.jwp.http.request.HttpCookie;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

public class SessionLogin {

    private final HttpCookie httpCookie;

    public SessionLogin(HttpCookie httpCookie) {
        this.httpCookie = httpCookie;
    }

    public boolean isSuccess() {
        final Optional<String> valueFromKey = httpCookie.valueFromKey(CookieType.JSESSIONID.value());
        if (valueFromKey.isEmpty()) {
            return false;
        }
        final HttpSession httpSession = HttpSessions.getSession(valueFromKey.get());
        return !Objects.isNull(httpSession);
    }
}

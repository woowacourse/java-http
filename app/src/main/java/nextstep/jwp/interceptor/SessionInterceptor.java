package nextstep.jwp.interceptor;

import java.util.UUID;
import nextstep.jwp.db.InMemorySessionRepository;
import nextstep.jwp.infrastructure.http.interceptor.HandlerInterceptor;
import nextstep.jwp.infrastructure.http.request.Cookie;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.model.Session;

public class SessionInterceptor implements HandlerInterceptor {

    private static final String SESSION_KEY = "JSESSIONID";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String KEY_VALUE_DELIMITER = "=";

    @Override
    public void preHandle(final HttpRequest request, final HttpResponse response) throws Exception {
        final Cookie cookie = request.getHeaders().getCookie();
        if (cookie.hasNotKey(SESSION_KEY)) {
            final String sessionId = String.valueOf(UUID.randomUUID());
            final String sessionCookie = String.join(KEY_VALUE_DELIMITER, SESSION_KEY, sessionId);
            request.addHeader(COOKIE_KEY, sessionCookie);
            response.addHeader(SET_COOKIE_KEY, sessionCookie);
            InMemorySessionRepository.save(new Session(sessionId));
        }
    }

    @Override
    public void postHandle(final HttpRequest request, final HttpResponse response) throws Exception {

    }
}

package nextstep.jwp.interceptor;

import java.util.UUID;
import nextstep.jwp.infrastructure.http.interceptor.HandlerInterceptor;
import nextstep.jwp.infrastructure.http.request.Cookie;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;

public class SessionInterceptor implements HandlerInterceptor {

    private static final String SESSION_KEY = "JSESSIONID";
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String KEY_VALUE_DELIMITER = "=";

    @Override
    public void preHandle(final HttpRequest request, final HttpResponse response) throws Exception {

    }

    @Override
    public void postHandle(final HttpRequest request, final HttpResponse response) throws Exception {
        final Cookie cookie = request.getHeaders().getCookie();
        if (!cookie.hasKey(SESSION_KEY)) {
            final String sessionId = String.valueOf(UUID.randomUUID());
            response.addHeader(SET_COOKIE_KEY, String.join(KEY_VALUE_DELIMITER, SESSION_KEY, sessionId));
        }
    }
}

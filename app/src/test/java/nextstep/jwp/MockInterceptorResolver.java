package nextstep.jwp;

import nextstep.jwp.db.InMemorySessionRepository;
import nextstep.jwp.infrastructure.http.interceptor.HandlerInterceptor;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.model.Session;

public class MockInterceptorResolver implements HandlerInterceptor {

    private static final String SESSION_KEY = "JSESSIONID";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String KEY_VALUE_DELIMITER = "=";

    public MockInterceptorResolver() {
    }

    @Override
    public void preHandle(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.hasNotCookie(SESSION_KEY)) {
            final String sessionId = "1234";
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

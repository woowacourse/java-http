package org.apache.coyote.http11.web;

import org.apache.coyote.http11.support.session.Session;
import org.apache.coyote.http11.support.session.SessionManager;
import org.apache.coyote.http11.web.request.HttpRequest;
import java.util.List;
import java.util.Optional;

public class AuthenticationInterceptor {

    private final List<String> includeUris;

    public AuthenticationInterceptor(final List<String> includeUris) {
        this.includeUris = includeUris;
    }

    public boolean preHandle(final HttpRequest httpRequest) {
        final Optional<Session> session = extractSession(httpRequest);
        final String uri = httpRequest.getUri();
        final boolean shouldNotHandle = includeUris.stream()
                .noneMatch(uri::contains);

        if (shouldNotHandle) {
            return true;
        }

        return session.isEmpty();
    }

    private Optional<Session> extractSession(final HttpRequest httpRequest) {
        if (httpRequest.hasCookie()) {
            final String rawSession = httpRequest.getSession();
            final SessionManager sessionManager = SessionManager.getInstance();
            final Session session = sessionManager.findSession(rawSession);
            return Optional.ofNullable(session);
        }

        return Optional.empty();
    }
}

package org.apache.coyote.request;

import org.apache.coyote.common.Headers;
import org.apache.coyote.session.Cookies;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.coyote.common.HeaderType.*;

public class RequestHeaders {

    private static final String HEADER_DELIMITER = ":";
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Headers headers;
    private final Cookies cookies;
    private final Session session;

    private RequestHeaders(final Headers headers, final Cookies cookies, final Session session) {
        this.headers = headers;
        this.cookies = cookies;
        this.session = session;
    }

    public static RequestHeaders from(final List<String> headersWithValue) {
        final Map<String, String> headerMapping = collectHeaderMapping(headersWithValue);
        final Cookies newCookies = getCookiesBy(headerMapping);
        final Session foundSession = getSessionBy(newCookies);

        return new RequestHeaders(new Headers(headerMapping), newCookies, foundSession);
    }

    private static Map<String, String> collectHeaderMapping(final List<String> headersWithValue) {
        return headersWithValue.stream()
                .map(headerWithValue -> headerWithValue.split(HEADER_DELIMITER))
                .collect(Collectors.toMap(
                        entry -> entry[HEADER_NAME_INDEX].trim(),
                        entry -> entry[HEADER_VALUE_INDEX].trim()
                ));
    }

    private static Cookies getCookiesBy(final Map<String, String> headers) {
        final String cookiesWithValue = headers.getOrDefault(COOKIE.value(), null);
        if (Objects.isNull(COOKIE.value())) {
            return Cookies.empty();
        }

        return Cookies.from(cookiesWithValue);
    }

    private static Session getSessionBy(final Cookies newCookies) {
        final String sessionId = newCookies.getCookieValue("JSESSIONID");
        if (Objects.isNull(sessionId)) {
            return Session.empty();
        }

        return SessionManager.findSession(sessionId);
    }

    public String getHeaderValue(final String headerName) {
        return headers.getHeaderValue(headerName);
    }

    public String getCookieValue(final String cookieName) {
        return cookies.getCookieValue(cookieName);
    }

    public Headers headers() {
        return headers;
    }

    public Cookies cookies() {
        return cookies;
    }

    public Session session() {
        return session;
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
               "headers=" + headers + System.lineSeparator() +
               "        " + cookies + System.lineSeparator() +
               "        " + session + System.lineSeparator() +
               '}';
    }
}

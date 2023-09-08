package org.apache.coyote.request;

import org.apache.coyote.common.Headers;
import org.apache.coyote.session.Cookies;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

import java.util.List;
import java.util.Objects;

import static org.apache.coyote.common.HeaderType.COOKIE;

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
        final Headers headers = collectHeaderMapping(headersWithValue);
        final Cookies cookies = getCookiesBy(headers);
        final Session session = getSessionBy(cookies);

        return new RequestHeaders(headers, cookies, session);
    }

    private static Headers collectHeaderMapping(final List<String> headersWithValue) {
        final Headers newHeaders = Headers.empty();

        headersWithValue.stream()
                .map(headerWithValue -> headerWithValue.split(HEADER_DELIMITER))
                .forEach(entry -> newHeaders.addHeader(
                        entry[HEADER_NAME_INDEX].strip(),
                        entry[HEADER_VALUE_INDEX].strip()
                ));

        return newHeaders;
    }

    private static Cookies getCookiesBy(final Headers headers) {
        final String cookieNamesAndValues = headers.getHeaderValue(COOKIE.value());
        if (Objects.isNull(cookieNamesAndValues)) {
            return Cookies.empty();
        }

        return Cookies.from(cookieNamesAndValues);
    }

    private static Session getSessionBy(final Cookies cookies) {
        final String sessionId = cookies.getCookieValue("JSESSIONID");
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

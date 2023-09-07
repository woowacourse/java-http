package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.SessionManager.SESSION_ID_COOKIE_NAME;

import java.net.URI;
import java.util.Map;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.SessionManager.Session;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.Method;
import org.apache.coyote.http11.common.header.EntityHeaders;
import org.apache.coyote.http11.common.header.GeneralHeaders;
import org.apache.coyote.http11.common.header.RequestHeaders;

public class Request {

    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private final Method method;
    private final String uri;
    private final GeneralHeaders generalHeaders;
    private final RequestHeaders requestHeaders;
    private final EntityHeaders entityHeaders;
    private final String body;

    // TODO RequestLine 포장, RequestLine에서 다시 uri의 queryString 가져올 수 있게 하기 (URI -> query -> StringFormatParser)
    private Request(
            final Method method,
            final String uri,
            final GeneralHeaders generalHeaders,
            final RequestHeaders requestHeaders,
            final EntityHeaders entityHeaders,
            final String body
    ) {
        this.method = method;
        this.uri = uri;
        this.generalHeaders = generalHeaders;
        this.requestHeaders = requestHeaders;
        this.entityHeaders = entityHeaders;
        this.body = body;
    }

    public static Request of(
            final String methodName,
            final String requestURI,
            final Map<String, String> headers,
            final String body
    ) {
        final var method = Method.find(methodName)
                .orElseThrow(() -> new IllegalArgumentException("invalid method"));

        return new Request(
                method,
                requestURI,
                new GeneralHeaders(headers),
                new RequestHeaders(headers),
                new EntityHeaders(headers),
                body
        );
    }

    public String getPath() {
        return URI.create(uri).getPath();
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public Session getSession() {
        return SESSION_MANAGER.findOrCreate(findSessionId());
    }

    private String findSessionId() {
        final var cookies = Cookies.from(requestHeaders.getCookie());

        return cookies.findByName(SESSION_ID_COOKIE_NAME);
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method=" + method +
                ", uri='" + uri + '\'' +
                ", generalHeaders=" + generalHeaders +
                ", requestHeaders=" + requestHeaders +
                ", entityHeaders=" + entityHeaders +
                ", body='" + body + '\'' +
                '}';
    }
}

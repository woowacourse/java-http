package org.apache.coyote.request;

import static org.apache.coyote.request.RequestHeader.ACCEPT;
import static org.apache.coyote.request.RequestHeader.COOKIE;
import static org.apache.coyote.utils.Constant.LINE_SEPARATOR;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.ContentType;
import org.apache.coyote.Cookies;
import org.apache.coyote.Headers;
import org.apache.coyote.Protocol;

public class HttpRequest {
    private static final int REQUEST_LINE_INDEX = 0;

    private final RequestLine requestLine;
    private final Headers headers;
    private final Cookies cookies;
    private String body;

    public HttpRequest(final RequestLine requestLine,
                       final Headers headers,
                       final Cookies cookies
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.cookies = cookies;
    }

    public static HttpRequest from(final String request) {
        final List<String> lines = new ArrayList<>(List.of(request.split(LINE_SEPARATOR)));

        final RequestLine requestLine = RequestLine.from(lines.remove(REQUEST_LINE_INDEX));
        final Headers headers = Headers.from(lines);
        final Cookies cookies = Cookies.from(headers.getValueOf(COOKIE));

        return new HttpRequest(
                requestLine,
                headers,
                cookies
        );
    }

    public Method getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getUri().getPath();
    }

    public Protocol getProtocol() {
        return requestLine.getProtocol();
    }

    public ContentType getContentType() {
        return ContentType.getContentType(headers.getValueOf(ACCEPT));
    }

    public String getHeader(final RequestHeader header) {
        return headers.getValueOf(header);
    }

    public boolean isCookieExist(final String cookieName) {
        return cookies.isExist(cookieName);
    }

    public String getCookie(final String cookieName) {
        return cookies.getValueOf(cookieName);
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }
}

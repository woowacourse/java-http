package org.apache.coyote.http11.message.response;

import java.util.stream.Collectors;
import org.apache.coyote.http11.message.ContentType;
import org.apache.coyote.http11.message.Cookie;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpProtocol;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.session.Session;

public class HttpResponse {

    private static final String DEFAULT_CHARSET = ";charset=utf-8";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String EMPTY_STRING = "";
    private static final String BLANK = " ";

    private final HttpProtocol httpProtocol;
    private HttpStatus httpStatus;
    private final HttpHeaders headers;
    private String body;

    private HttpResponse(final HttpStatus httpStatus, final HttpHeaders headers, final String body) {
        this.httpProtocol = HttpProtocol.HTTP_ONE;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse create() {
        return new HttpResponse(null, HttpHeaders.empty(), EMPTY_STRING);
    }

    public void setJSessionCookieBySession(final Session session) {
        final Cookie cookie = new Cookie("JSESSIONID", session.getId());
        headers.setHeaderWithValue(SET_COOKIE, cookie.getAllNamesWithValue());
    }

    public void setBody(final String body, final ContentType contentType) {
        this.body = body;
        setHeader("Content-Type", contentType.getType() + DEFAULT_CHARSET);
        setHeader("Content-Length", String.valueOf(body.getBytes().length));
    }

    public void setHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setHeader(final String field, final String value) {
        headers.setHeaderWithValue(field, value);
    }

    public String convertToMessage() {
        return String.join("\r\n",
            createStatusLine(),
            createHeaderLines(),
            EMPTY_STRING,
            body);
    }

    private String createStatusLine() {
        return String.join(BLANK,
            httpProtocol.getVersion(), String.valueOf(httpStatus.getStatusCode()),
            httpStatus.getDescription(), EMPTY_STRING);
    }

    private String createHeaderLines() {
        return headers.getValuesByHeaderField()
            .entrySet()
            .stream()
            .map(entry -> String.join(HttpHeaders.FIELD_VALUE_DELIMITER, entry.getKey(), entry.getValue()) + BLANK)
            .collect(Collectors.joining("\r\n"));
    }
}

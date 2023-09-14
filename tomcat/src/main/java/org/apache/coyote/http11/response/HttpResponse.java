package org.apache.coyote.http11.response;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.coyote.HttpVersion;
import org.apache.coyote.http11.converter.HeaderStringConverter;
import org.apache.coyote.http11.header.Headers;

public class HttpResponse {

    private static final List<String> EMPTY_HEADERS = Collections.emptyList();
    private static final String EMPTY_BODY = "";

    private StatusLine statusLine;
    private Headers headers;
    private HttpCookie cookie = new HttpCookie(Collections.emptyMap());
    private String body;

    public HttpResponse(
        final StatusCode statusCode,
        final List<String> headers,
        final String body
    ) {
        this.statusLine = new StatusLine(HttpVersion.HTTP_1_1, statusCode);
        this.headers = new Headers(headers);
        this.body = body;
    }

    public static HttpResponse simpleBody(
        final StatusCode statusCode,
        final ContentType contentType,
        final String body
    ) {
        final List<String> headers = List.of(
            HeaderStringConverter.toContentType(contentType),
            HeaderStringConverter.toContentLength(body)
        );

        return new HttpResponse(statusCode, headers, body);
    }

    public static HttpResponse ok() {
        return new HttpResponse(StatusCode.OK, EMPTY_HEADERS, EMPTY_BODY);
    }

    public static HttpResponse ok(
        final ContentType contentType,
        final String body
    ) {
        return simpleBody(StatusCode.OK, contentType, body);
    }

    public static HttpResponse notFound() {
        return new HttpResponse(StatusCode.NOT_FOUND, EMPTY_HEADERS, EMPTY_BODY);
    }

    public static HttpResponse redirect(final String locationUrl) {
        return new HttpResponse(
            StatusCode.FOUND,
            Collections.singletonList(HeaderStringConverter.toLocation(locationUrl)),
            EMPTY_BODY
        );
    }

    public void update(final HttpResponse other) {
        if (this.statusLine != other.statusLine) {
            this.statusLine = other.statusLine;
        }
        if (!this.headers.containsAll(other.headers)) {
            this.headers = other.headers;
        }
        if (!this.cookie.getValues().equals(other.getCookie().getValues())) {
            this.cookie = other.cookie;
        }
        if (!this.body.equals(other.body)) {
            this.body = other.body;
        }
    }

    public void addCookie(final String name, final String value) {
        cookie.addCookie(name, value);
    }

    public String convertToMessage() {
        final StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(makeStatusLine())
            .append(System.lineSeparator())
            .append(headers.toMessageFormat());

        if (!cookie.isEmpty()) {
            messageBuilder.append(cookie.toMessageFormat());
        }
        messageBuilder.append(System.lineSeparator())
            .append(body);

        return messageBuilder.toString();
    }

    private String makeStatusLine() {
        final String statusLineFormat = "%s %d %s ";

        return String.format(
            statusLineFormat,
            statusLine.getHttpVersion().getValue(),
            statusLine.getStatusCode().getCode(),
            statusLine.getReasonPhrase()
        );
    }

    public HttpVersion getHttpVersion() {
        return statusLine.getHttpVersion();
    }

    public StatusCode getStatusCode() {
        return statusLine.getStatusCode();
    }

    public String getStatusMessage() {
        return statusLine.getReasonPhrase();
    }

    public Map<String, String> getHeaders() {
        return headers.getAllHeaders();
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public String getBody() {
        return body;
    }
}


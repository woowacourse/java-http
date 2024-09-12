package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String CHARSET_UTF_8 = "; charset=utf-8";
    private static final String CRLF = "\r\n";
    private static final String BLANK = " ";

    private final String version;
    private final HttpStatus httpStatus;
    private final Map<HttpHeader, String> headers;
    private final String body;

    private HttpResponse(final String version,
                         final HttpStatus httpStatus,
                         final Map<HttpHeader, String> headers,
                         final String body) {
        this.version = version;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(final String version,
                                  final HttpStatus HttpStatus,
                                  final ContentType contentType,
                                  final String body) {
        final Map<HttpHeader, String> headers = new HashMap<>();
        headers.put(HttpHeader.CONTENT_TYPE, contentType.getContentType() + CHARSET_UTF_8);
        headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return new HttpResponse(version, HttpStatus, headers, body);
    }

    public static HttpResponse of(final String version,
                                  final HttpStatus HttpStatus,
                                  final ContentType contentType) {
        final Map<HttpHeader, String> headers = new HashMap<>();
        headers.put(HttpHeader.CONTENT_TYPE, contentType.getContentType() + CHARSET_UTF_8);
        return new HttpResponse(version, HttpStatus, headers, null);
    }

    public void addHeader(HttpHeader key, String value) {
        headers.put(key, value);
    }

    public String toHttpMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(version).append(BLANK).append(httpStatus.getHttpStatusMessage()).append(BLANK).append(CRLF);
        headers.forEach((key, value) -> stringBuilder.append(key.getHeaderName()).append(": ").append(value).append(BLANK).append(CRLF));
        stringBuilder.append(CRLF);
        stringBuilder.append(body);

        return stringBuilder.toString();
    }
}

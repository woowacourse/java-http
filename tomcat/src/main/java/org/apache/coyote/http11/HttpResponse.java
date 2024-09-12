package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String CHARSET_UTF_8 = "; charset=utf-8";
    private static final String CRLF = "\r\n";
    private static final String BLANK = " ";

    private final String version;
    private final String httpStatusCode;
    private final Map<String, String> headers;
    private final String contentType;
    private final String body;

    private HttpResponse(final String version,
                         final String httpStatusCode,
                         final Map<String, String> headers,
                         final String contentType,
                         final String body) {
        this.version = version;
        this.httpStatusCode = httpStatusCode;
        this.headers = headers;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse of(final String version,
                                  final String httpStatusCode,
                                  final String contentType,
                                  final String body) {
        final Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.CONTENT_TYPE.getHeaderName(), contentType + CHARSET_UTF_8);
        headers.put(HttpHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(body.getBytes().length));
        return new HttpResponse(version, httpStatusCode, headers, contentType, body);
    }

    public static HttpResponse of(final String version,
                                  final String httpStatusCode,
                                  final String contentType) {
        final Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.CONTENT_TYPE.getHeaderName(), contentType + CHARSET_UTF_8);
        return new HttpResponse(version, httpStatusCode, headers, contentType, null);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String toHttpMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(version).append(BLANK).append(httpStatusCode).append(BLANK).append(CRLF);
        headers.forEach((key, value) -> stringBuilder.append(key).append(": ").append(value).append(BLANK).append(CRLF));
        stringBuilder.append(CRLF);
        stringBuilder.append(body);

        return stringBuilder.toString();
    }
}

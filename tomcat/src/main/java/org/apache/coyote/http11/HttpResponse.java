package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

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
        headers.put("Content-Type", contentType + "; charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        return new HttpResponse(version, httpStatusCode, headers, contentType, body);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String toHttpMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(version).append(" ").append(httpStatusCode).append(" \r\n");
        headers.forEach((key, value) -> stringBuilder.append(key).append(": ").append(value).append(" \r\n"));
        stringBuilder.append("\r\n");
        stringBuilder.append(body);

        return stringBuilder.toString();
    }
}

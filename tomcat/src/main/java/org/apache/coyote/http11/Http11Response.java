package org.apache.coyote.http11;

import java.util.Map;

public class Http11Response {
    private final String httpVersion;
    private final int httpStatusCode;
    private final String httpStatusMessage;
    private final Map<String, String> headers;
    private final String body;

    public Http11Response(final String httpVersion, final int httpStatusCode, final String httpStatusMessage, final Map<String, String> headers, final String body) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.httpStatusMessage = httpStatusMessage;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String toString() {
        final StringBuilder line = new StringBuilder();
        line.append(String.format("%s %s %s \r\n", httpVersion, httpStatusCode, httpStatusMessage));
        for (String key : headers.keySet()) {
            line.append(String.format("%s: %s\r\n", key, headers.getOrDefault(key, "")));
        }
        line.append("\r\n");
        line.append(body);
        return line.toString();
    }
}

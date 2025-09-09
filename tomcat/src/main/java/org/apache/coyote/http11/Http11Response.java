package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Http11Response {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final String httpVersion;
    private final int httpStatusCode;
    private final String httpStatusMessage;
    private final Map<String, String> headers;
    private final String body;

    public Http11Response(
            final String httpVersion,
            final HttpStatus httpStatus,
            final Map<String, String> headers,
            final String contentType,
            final byte[] body
    ) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatus.getCode();
        this.httpStatusMessage = httpStatus.getMessage();
        headers.put(CONTENT_TYPE, contentType);
        headers.put(CONTENT_LENGTH, String.valueOf(body.length));
        this.headers = headers;
        this.body = new String(body, StandardCharsets.UTF_8);
    }

    public Http11Response(
            final String httpVersion,
            final HttpStatus httpStatus,
            final String contentType,
            final byte[] body
    ) {
        final Map<String, String> headers = new LinkedHashMap<>();
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatus.getCode();
        this.httpStatusMessage = httpStatus.getMessage();
        headers.put(CONTENT_TYPE, contentType);
        headers.put(CONTENT_LENGTH, String.valueOf(body.length));
        this.headers = headers;
        this.body = new String(body, StandardCharsets.UTF_8);
    }

    private Http11Response(
            final String httpVersion,
            final int httpStatusCode,
            final String httpStatusMessage,
            final Map<String, String> headers,
            final String body
    ) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.httpStatusMessage = httpStatusMessage;
        this.headers = headers;
        this.body = body;
    }

    public static Http11Response createHtmlResponse(final HttpStatus httpStatus, final byte[] body) {
        return new Http11Response(
                "HTTP/1.1",
                httpStatus,
                "text/html;charset=utf-8",
                body
        );
    }

    public static Http11Response createHtmlResponse(final HttpStatus httpStatus, final Map<String, String> headers, final byte[] body) {
        return new Http11Response(
                "HTTP/1.1",
                httpStatus,
                headers,
                "text/html;charset=utf-8",
                body
        );
    }

    public static Http11Response createCssResponse(final HttpStatus httpStatus,  final byte[] body) {
        return new Http11Response(
                "HTTP/1.1",
                httpStatus,
                "text/css;charset=utf-8",
                body
        );
    }

    public static Http11Response createJsResponse(final HttpStatus httpStatus, final byte[] body) {
        return new Http11Response(
                "HTTP/1.1",
                httpStatus,
                "application/javascript;charset=utf-8",
                body
        );
    }

    public byte[] toMessage() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        final List<String> headerStrings = new ArrayList<>();
        for (final String key : headers.keySet()) {
            headerStrings.add(String.format("%s: %s ", key, headers.get(key)));
        }
        final String headerString = String.join("\r\n", headerStrings);

        return String.join("\r\n",
                String.format("%s %s %s ", httpVersion, httpStatusCode, httpStatusMessage),
                headerString,
                "",
                body
                );
    }
}

package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Http11Response {

    private static final String REDIRECT_BASE_URL = "http://localhost:8080";
    private static final String HTTP11_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
    private static final String JS_CONTENT_TYPE = "application/javascript;charset=utf-8";
    private static final String SVG_CONTENT_TYPE = "image/svg+xml;charset=utf-8";

    private final String httpVersion;
    private final int httpStatusCode;
    private final String httpStatusMessage;
    private final Map<String, String> headers;
    private final String body;

    public static Http11Response createHtmlResponse(final HttpStatus httpStatus, final byte[] body) {
        return new Http11Response(
                HTTP11_VERSION,
                httpStatus,
                new LinkedHashMap<>(),
                HTML_CONTENT_TYPE,
                body
        );
    }

    public static Http11Response createCssResponse(final HttpStatus httpStatus, final byte[] body) {
        return new Http11Response(
                HTTP11_VERSION,
                httpStatus,
                new LinkedHashMap<>(),
                CSS_CONTENT_TYPE,
                body
        );
    }

    public static Http11Response createJsResponse(final HttpStatus httpStatus, final byte[] body) {
        return new Http11Response(
                HTTP11_VERSION,
                httpStatus,
                new LinkedHashMap<>(),
                JS_CONTENT_TYPE,
                body
        );
    }

    public static Http11Response createSvgResponse(final HttpStatus httpStatus, final byte[] body) {
        return new Http11Response(
                HTTP11_VERSION,
                httpStatus,
                new LinkedHashMap<>(),
                SVG_CONTENT_TYPE,
                body
        );
    }

    public static Http11Response createRedirectResponse(final String redirectTarget) {
        return createRedirectResponse(redirectTarget, new LinkedHashMap<>());
    }

    public static Http11Response createRedirectResponse(final String redirectTarget, final Map<String, String> headers) {
        headers.put(LOCATION, REDIRECT_BASE_URL + redirectTarget);

        return new Http11Response(
                HTTP11_VERSION,
                HttpStatus.FOUND,
                headers,
                HTML_CONTENT_TYPE,
                new byte[0]
        );
    }

    private Http11Response(
            final String httpVersion,
            final HttpStatus httpStatus,
            final Map<String, String> headers,
            final String contentType,
            final byte[] body
    ) {
        this(
                httpVersion,
                httpStatus.getCode(),
                httpStatus.getMessage(),
                headers,
                new String(body, StandardCharsets.UTF_8)
        );
        headers.put(CONTENT_TYPE, contentType);
        headers.put(CONTENT_LENGTH, String.valueOf(body.length));
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

        if (body == null) {
            return String.join("\r\n",
                    String.format("%s %s %s ", httpVersion, httpStatusCode, httpStatusMessage),
                    headerString,
                    ""
            );
        }
        return String.join("\r\n",
                String.format("%s %s %s ", httpVersion, httpStatusCode, httpStatusMessage),
                headerString,
                "",
                body
        );
    }
}

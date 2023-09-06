package org.apache.coyote.http.response;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpVersion;

public class Response {

    private final HttpVersion version;
    private final HttpStatusCode statusCode;
    private final HttpResponseHeaders headers;
    private final List<HttpCookie> cookies;
    private final String body;

    private Response(
            final HttpVersion version,
            final HttpStatusCode statusCode,
            final HttpResponseHeaders headers,
            final String body
    ) {
        this(version, statusCode, headers, Collections.emptyList(), body);
    }

    private Response(
            final HttpVersion version,
            final HttpStatusCode statusCode,
            final HttpResponseHeaders headers,
            final List<HttpCookie> cookies,
            final String body
    ) {
        this.version = version;
        this.statusCode = statusCode;
        this.headers = headers;
        this.cookies = cookies;
        this.body = body;
    }

    public static Response of(
            final Request request,
            final HttpStatusCode statusCode,
            final ContentType contentType,
            final String body,
            final HeaderDto... headerContents
    ) {
        final HttpResponseHeaders headers = HttpResponseHeaders.of(contentType, body, headerContents);

        return new Response(request.version(), statusCode, headers, body);
    }

    public static Response of(
            final Request request,
            final HttpStatusCode statusCode,
            final ContentType contentType,
            final String body,
            final List<HttpCookie> cookies,
            final HeaderDto... headerContents
    ) {
        final HttpResponseHeaders headers = HttpResponseHeaders.of(contentType, body, headerContents);

        return new Response(request.version(), statusCode, headers, cookies, body);
    }

    public String convertResponseMessage() {
        if (cookies.isEmpty()) {
            return String.join(HttpConsts.CRLF,
                    convertStartLine(),
                    headers.convertHeaders(),
                    HttpConsts.BLANK,
                    body);
        }

        return String.join(HttpConsts.CRLF,
                convertStartLine(),
                headers.convertHeaders(),
                convertCookies(),
                HttpConsts.BLANK,
                body);
    }

    private String convertStartLine() {
        return version.versionContent() + HttpConsts.SPACE + statusCode.convertHttpStatusMessage() + HttpConsts.SPACE;
    }

    private String convertCookies() {
        return cookies.stream().map(HttpCookie::convertHeaders).collect(Collectors.joining(HttpConsts.CRLF));
    }
}

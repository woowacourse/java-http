package org.apache.coyote.http.response;

import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpVersion;

public class Response {

    private final HttpVersion version;
    private final HttpStatusCode statusCode;
    private final HttpResponseHeaders headers;
    private final HttpCookie cookie;
    private final String body;

    private Response(
            final HttpVersion version,
            final HttpStatusCode statusCode,
            final HttpResponseHeaders headers,
            final String body
    ) {
        this(version, statusCode, headers, HttpCookie.EMPTY, body);
    }

    private Response(
            final HttpVersion version,
            final HttpStatusCode statusCode,
            final HttpResponseHeaders headers,
            final HttpCookie cookie,
            final String body
    ) {
        this.version = version;
        this.statusCode = statusCode;
        this.headers = headers;
        this.cookie = cookie;
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
            final HttpCookie cookie,
            final HeaderDto... headerContents
    ) {
        final HttpResponseHeaders headers = HttpResponseHeaders.of(contentType, body, headerContents);

        return new Response(request.version(), statusCode, headers, cookie, body);
    }

    public String convertResponseMessage() {
        if (HttpCookie.EMPTY.equals(cookie)) {
            return String.join(HttpConsts.CRLF,
                    convertStartLine(),
                    headers.convertHeaders(),
                    HttpConsts.BLANK,
                    body);
        }
        return String.join(HttpConsts.CRLF,
                convertStartLine(),
                headers.convertHeaders(),
                cookie.convertHeaders(),
                HttpConsts.BLANK,
                body);
    }

    private String convertStartLine() {
        return version.versionContent() + HttpConsts.SPACE + statusCode.convertHttpStatusMessage() + HttpConsts.SPACE;
    }


}

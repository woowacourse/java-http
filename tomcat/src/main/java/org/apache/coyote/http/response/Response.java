package org.apache.coyote.http.response;

import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpVersion;

public class Response {

    private final HttpVersion version;
    private final HttpStatusCode statusCode;
    private final HttpResponseHeaders headers;
    private final String body;

    private Response(
            final HttpVersion version,
            final HttpStatusCode statusCode,
            final HttpResponseHeaders headers,
            final String body
    ) {
        this.version = version;
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public static Response of(
            final Request request,
            final HttpStatusCode statusCode,
            final ContentType contentType,
            final String body
    ) {
        final HttpResponseHeaders headers = HttpResponseHeaders.of(contentType, body);

        return new Response(request.version(), statusCode, headers, body);
    }

    public String convertResponseMessage() {
        return String.join(
                HttpConsts.CRLF,
                convertStartLine(),
                headers.convertHeaders(),
                HttpConsts.BLANK,
                body
        );
    }

    private String convertStartLine() {
        return version.versionContent() + HttpConsts.SPACE + statusCode.convertHttpStatusMessage() + HttpConsts.SPACE;
    }
}

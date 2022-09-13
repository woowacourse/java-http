package org.apache.coyote.http11.response;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpVersion;
import org.apache.coyote.http11.session.Session;

public class HttpResponse {

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final HttpResponseHeaders httpHeaders;
    private final String responseBody;

    private HttpResponse(final HttpVersion httpVersion,
                         final HttpStatus httpStatus,
                         final HttpResponseHeaders httpHeaders,
                         final String responseBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse found(final HttpVersion httpVersion,
                                     final ContentType contentType,
                                     final String redirectUrl) {
        final HttpResponseHeaders headers = new HttpResponseHeaders();
        headers.add("Content-Type", contentType.getValue());
        headers.add("Location", redirectUrl);

        return new HttpResponse(httpVersion, HttpStatus.FOUND, headers, "");
    }

    public static HttpResponse ok(final HttpVersion httpVersion,
                                  final ContentType contentType,
                                  final String responseBody) {
        final HttpResponseHeaders headers = new HttpResponseHeaders();
        headers.add("Content-Type", contentType.getValue());
        headers.add("Content-Length", String.valueOf(responseBody.getBytes().length));

        return new HttpResponse(httpVersion, HttpStatus.OK, headers, responseBody);
    }

    public void addCookie(final Session session) {
        httpHeaders.add("Set-Cookie", String.format("JSESSIONID=%s", session.getId()));
    }

    public String createResponse() {
        return String.join("\r\n",
                getStartLine(),
                httpHeaders.toResponse(),
                responseBody);
    }

    private String getStartLine() {
        return String.format("%s %s ", httpVersion.getValue(), httpStatus.getValue());
    }
}

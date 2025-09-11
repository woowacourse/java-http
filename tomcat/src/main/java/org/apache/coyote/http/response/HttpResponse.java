package org.apache.coyote.http.response;

import static org.apache.coyote.http.common.HttpConstants.CRLF;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.http.common.ContentType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponse {

    public static final String LOCATION_HEADER_NAME = "Location";

    private final HttpStatusLine statusLine;
    private final HttpResponseHeader header;
    private final HttpResponseBody body;

    public static HttpResponse from(
            final HttpStatusLine statusLine,
            final HttpResponseHeader header,
            final HttpResponseBody body
    ) {
        header.setContentLength(body.getContentLength());
        return new HttpResponse(statusLine, header, body);
    }

    public static HttpResponse redirect(final String version, final String location) {
        final HttpStatusLine statusLine = HttpStatusLine.from(version, HttpStatus.FOUND);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.HTML);
        header.add(LOCATION_HEADER_NAME, location);
        header.setContentLength(0);
        final HttpResponseBody body = HttpResponseBody.empty();

        return new HttpResponse(statusLine, header, body);
    }

    public void setCookie(final String name, final String value) {
        header.addSetCookie(name, value);
    }

    public int getContentLength() {
        return body.getContentLength();
    }

    @Override
    public String toString() {
        return "%s%s%s%s".formatted(statusLine, header, CRLF, body);
    }
}

package org.apache.coyote.http.response;

import static org.apache.coyote.http.common.HttpConstants.CONTENT_LENGTH_HEADER_NAME;
import static org.apache.coyote.http.common.HttpConstants.CRLF;
import static org.apache.coyote.http.common.HttpConstants.HEADER_VALUE_SEPARATOR;

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
        return new HttpResponse(statusLine, header, body);
    }

    public static HttpResponse redirect(final String version, final String location) {
        final HttpStatusLine statusLine = HttpStatusLine.from(version, HttpStatus.FOUND);
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.HTML);
        header.add(LOCATION_HEADER_NAME, location);
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
        final String contentLengthHeader =
                CONTENT_LENGTH_HEADER_NAME + HEADER_VALUE_SEPARATOR + " " + getContentLength() + CRLF;
        return "%s%s%s%s%s".formatted(statusLine, header, contentLengthHeader, CRLF, body);
    }
}

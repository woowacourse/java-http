package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.common.ContentType.withCharset;
import static org.apache.coyote.http11.common.Status.FOUND;
import static org.apache.coyote.http11.common.header.HeaderName.CONTENT_LENGTH;
import static org.apache.coyote.http11.common.header.HeaderName.CONTENT_TYPE;
import static org.apache.coyote.http11.common.header.HeaderName.LOCATION;

import java.util.Map;
import org.apache.coyote.http11.common.Status;
import org.apache.coyote.http11.common.header.EntityHeaders;
import org.apache.coyote.http11.common.header.GeneralHeaders;
import org.apache.coyote.http11.common.header.HeaderName;
import org.apache.coyote.http11.common.header.ResponseHeaders;

public class Response {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final Status status;
    private final GeneralHeaders generalHeaders;
    private final ResponseHeaders responseHeaders;
    private final EntityHeaders entityHeaders;
    private final String body;

    private Response(
            final Status status,
            final GeneralHeaders generalHeaders,
            final ResponseHeaders responseHeaders,
            final EntityHeaders entityHeaders,
            final String body
    ) {
        this.status = status;
        this.generalHeaders = generalHeaders;
        this.responseHeaders = responseHeaders;
        this.entityHeaders = entityHeaders;
        this.body = body;
    }

    public static Response of(
            final Status status,
            final Map<HeaderName, String> allHeaders,
            final String body
    ) {
        return new Response(
                status,
                new GeneralHeaders(allHeaders),
                new ResponseHeaders(allHeaders),
                new EntityHeaders(allHeaders),
                body
        );
    }

    public static Response of(
            final Status status,
            final String contentTypeString,
            final String body
    ) {
        return new Response(
                status,
                new GeneralHeaders(),
                new ResponseHeaders(),
                new EntityHeaders(Map.of(
                        CONTENT_TYPE, contentTypeString,
                        CONTENT_LENGTH, String.valueOf(body.getBytes().length)
                )),
                body
        );
    }

    public static Response redirect(final String location) {
        return Response.of(FOUND, Map.of(LOCATION, location, CONTENT_TYPE, withCharset(HTML.toString(), "utf-8")), "");
    }

    public void addSetCookie(final String cookie) {
        responseHeaders.addSetCookie(cookie);
    }

    public Status getStatus() {
        return status;
    }

    public String getLocation() {
        return responseHeaders.getLocation();
    }

    public String getContentType() {
        return entityHeaders.getContentType();
    }

    public String getBody() {
        return body;
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        return String.join(LINE_SEPARATOR,
                "HTTP/1.1 " + status.getCode() + " " + status.name(),
                generalHeaders.toString() + responseHeaders.toString() + entityHeaders.toString(),
                body);
    }
}

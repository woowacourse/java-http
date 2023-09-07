package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.common.Status.FOUND;
import static org.apache.coyote.http11.common.Status.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.common.Status.METHOD_NOT_ALLOWED;
import static org.apache.coyote.http11.common.Status.NOT_FOUND;
import static org.apache.coyote.http11.common.Status.OK;

import org.apache.coyote.http11.common.Protocol;
import org.apache.coyote.http11.common.Status;
import org.apache.coyote.http11.common.header.EntityHeaders;
import org.apache.coyote.http11.common.header.GeneralHeaders;
import org.apache.coyote.http11.common.header.ResponseHeaders;

public class Response {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final StatusLine statusLine;
    private final GeneralHeaders generalHeaders;
    private final ResponseHeaders responseHeaders;
    private final EntityHeaders entityHeaders;
    private final String body;

    private Response(
            final StatusLine statusLine,
            final GeneralHeaders generalHeaders,
            final ResponseHeaders responseHeaders,
            final EntityHeaders entityHeaders,
            final String body
    ) {
        this.statusLine = statusLine;
        this.generalHeaders = generalHeaders;
        this.responseHeaders = responseHeaders;
        this.entityHeaders = entityHeaders;
        this.body = body;
    }

    public Response(final Builder builder) {
        this(
                new StatusLine(Protocol.HTTP11, builder.status),
                builder.generalHeaders,
                builder.responseHeaders,
                builder.entityHeaders,
                builder.body
        );
    }

    public static Builder ok() {
        return new Builder(OK);
    }

    public static Builder notFound() {
        return new Builder(NOT_FOUND);
    }

    public static Builder redirect(final String location) {
        return Response.builder(FOUND)
                .addContentType(HTML.toString())
                .addLocation(location);
    }

    public static Builder methodNotAllowed() {
        return Response.builder(METHOD_NOT_ALLOWED);
    }

    public static Builder builder(final Status status) {
        return new Builder(status);
    }

    public static Builder internalSeverError() {
        return Response.builder(INTERNAL_SERVER_ERROR);
    }

    public Status getStatus() {
        return statusLine.getStatus();
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
        final var headerGroup = String.join(
                generalHeaders.toString(),
                responseHeaders.toString(),
                entityHeaders.toString()
        );

        return String.join(LINE_SEPARATOR,
                statusLine.toString(),
                headerGroup,
                body);
    }

    public static class Builder {
        private final Status status;
        private final GeneralHeaders generalHeaders = new GeneralHeaders();
        private final ResponseHeaders responseHeaders = new ResponseHeaders();
        private final EntityHeaders entityHeaders = new EntityHeaders();
        private String body;

        public Builder(final Status status) {
            this.status = status;
        }

        public Builder addSetCookie(final String cookie) {
            this.responseHeaders.addSetCookie(cookie);
            return this;
        }

        public Builder addLocation(final String location) {
            this.responseHeaders.addLocation(location);
            return this;
        }

        public Builder addContentType(final String contentType) {
            this.entityHeaders.addContentType(contentType);
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            this.entityHeaders.addContentLength(body);
            return this;
        }

        public Response build() {
            if (body == null) {
                body = "";
            }
            return new Response(this);
        }

    }
}

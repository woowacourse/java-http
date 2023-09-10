package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.MimeType.HTML;
import static org.apache.coyote.http11.common.Status.FOUND;
import static org.apache.coyote.http11.common.Status.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.common.Status.METHOD_NOT_ALLOWED;
import static org.apache.coyote.http11.common.Status.NOT_FOUND;
import static org.apache.coyote.http11.common.Status.OK;

import org.apache.catalina.servlet.util.StaticResource;
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

    public Response(final ServletResponse servletResponse) {
        this(new StatusLine(Protocol.HTTP11, servletResponse.status),
                servletResponse.generalHeaders,
                servletResponse.responseHeaders,
                servletResponse.entityHeaders,
                servletResponse.body);
    }

    public static ServletResponse ok() {
        return new ServletResponse(OK);
    }

    public static ServletResponse notFound() {
        return new ServletResponse(NOT_FOUND);
    }

    public static ServletResponse redirect(final String location) {
        return Response.builder(FOUND)
                .addContentType(HTML.toString())
                .addLocation(location);
    }

    public static ServletResponse methodNotAllowed() {
        return Response.builder(METHOD_NOT_ALLOWED);
    }

    public static ServletResponse builder(final Status status) {
        return new ServletResponse(status);
    }

    // TODO ServletResponse 클래스 분리
    public static ServletResponse internalSeverError() {
        return Response.builder(INTERNAL_SERVER_ERROR);
    }

    public static ServletResponse staticResource(final StaticResource staticResource) {
        return Response.ok()
                .body(staticResource.getContentBytes())
                .addContentType(staticResource.getContentType());
    }

    public Status getStatus() {
        return statusLine.getStatus();
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

    public static class ServletResponse {
        private final GeneralHeaders generalHeaders = new GeneralHeaders();
        private final ResponseHeaders responseHeaders = new ResponseHeaders();
        private final EntityHeaders entityHeaders = new EntityHeaders();
        private Status status;
        private String body;

        public ServletResponse() {
        }

        public ServletResponse(final Status status) {
            this.status = status;
        }

        // TODO ServeletResponse 대신 ResponseEntity 사용
        public void set(final ServletResponse response) {
            generalHeaders.addAll(response.generalHeaders);
            responseHeaders.addAll(response.responseHeaders);
            entityHeaders.addAll(response.entityHeaders);
            status = response.status;
            body = response.body;
        }

        public ServletResponse addSetCookie(final String cookie) {
            this.responseHeaders.addSetCookie(cookie);
            return this;
        }

        public ServletResponse addLocation(final String location) {
            this.responseHeaders.addLocation(location);
            return this;
        }

        public ServletResponse addContentType(final String contentType) {
            this.entityHeaders.addContentType(contentType);
            return this;
        }

        public ServletResponse body(final String body) {
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

        public Status getStatus() {
            return status;
        }

        public void setStatus(final Status status) {
            this.status = status;
        }

        public String getBody() {
            return body;
        }

        public String getLocation() {
            return responseHeaders.getLocation();
        }
    }
}

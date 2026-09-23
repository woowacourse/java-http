package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String CONTENT_LENGTH = "Content-Length";

    private static final String LOCATION = "Location";

    private static final byte[] EMPTY_BODY = new byte[0];

    private HttpStatus status;
    private String contentType;

    private final Map<String, String> headers = new LinkedHashMap<>();

    private byte[] body = EMPTY_BODY;

    public void ok(final String contentType, final byte[] body) {
        this.status = HttpStatus.OK;
        this.contentType = contentType;
        this.body = body;
    }

    public void notFound(final String contentType, final byte[] body) {
        this.status = HttpStatus.NOT_FOUND;
        this.contentType = contentType;
        this.body = body;
    }

    public void sendRedirect(final String location) {
        this.status = HttpStatus.FOUND;
        this.contentType = null;
        this.body = EMPTY_BODY;

        addHeader(LOCATION, location);
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public boolean hasStatus() {
        return status != null;
    }

    public void writeTo(final OutputStream outputStream) throws IOException {
        final StringBuilder responseHeaders = new StringBuilder();

        responseHeaders
                .append(HTTP_VERSION)
                .append(" ")
                .append(status.getCode())
                .append(" ")
                .append(status.getReasonPhrase())
                .append(" \r\n");

        if (contentType != null) {
            responseHeaders
                    .append(CONTENT_TYPE)
                    .append(": ")
                    .append(contentType)
                    .append(" \r\n");
        }

        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseHeaders
                    .append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(" \r\n");
        }

        responseHeaders
                .append(CONTENT_LENGTH)
                .append(": ")
                .append(body.length)
                .append(" \r\n")
                .append("\r\n");

        outputStream.write(responseHeaders.toString()
                .getBytes(StandardCharsets.UTF_8));

        outputStream.write(body);
        outputStream.flush();
    }
}
package org.apache.coyote.http11.response;

import org.apache.catalina.core.servlet.HttpServletResponse;
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

    public Response(final HttpServletResponse httpServletResponse) {
        this(new StatusLine(Protocol.HTTP11, httpServletResponse.getStatus()),
                httpServletResponse.getGeneralHeaders(),
                httpServletResponse.getResponseHeaders(),
                httpServletResponse.getEntityHeaders(),
                httpServletResponse.getBody());
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
    
}

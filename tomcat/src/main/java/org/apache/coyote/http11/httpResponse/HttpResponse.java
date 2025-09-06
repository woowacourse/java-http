package org.apache.coyote.http11.httpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private final HttpResponseHeader httpResponseHeader;
    private final HttpResponseBody httpResponseBody;

    public HttpResponse(
            final String requestUri
    ) {
        this.httpResponseHeader = new HttpResponseHeader(requestUri);
        this.httpResponseBody = new HttpResponseBody(requestUri);
    }

    public byte[] getBytes() throws IOException {
        final HttpResponseContent content = httpResponseBody.getContent();
        final HttpStatus httpStatus = content.httpStatus();

        final String body = content.body();
        final int bodyLength = body.getBytes(StandardCharsets.UTF_8).length;

        final String location = content.location();

        final String header = httpResponseHeader.getHeader(httpStatus, bodyLength, location);

        return (header + body).getBytes(StandardCharsets.UTF_8);
    }
}

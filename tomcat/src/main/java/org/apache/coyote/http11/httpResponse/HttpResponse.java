package org.apache.coyote.http11.httpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
        final String body = httpResponseBody.getBody();
        int bodyLength = body.getBytes(StandardCharsets.UTF_8).length;

        final String header = httpResponseHeader.getHeader(bodyLength);

        return (header + body).getBytes(StandardCharsets.UTF_8);
    }
}

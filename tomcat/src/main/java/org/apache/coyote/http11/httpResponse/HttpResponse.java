package org.apache.coyote.http11.httpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public class HttpResponse {

    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    public HttpResponse(
            final HttpRequest httpRequest
    ) {
        this.responseHeader = new ResponseHeader(httpRequest.getPath());
        this.responseBody = new ResponseBody(httpRequest);
    }

    public byte[] getBytes() throws IOException {
        final ResponseContent content = responseBody.getContent();
        final HttpStatus httpStatus = content.httpStatus();

        final String body = content.body();
        final int bodyLength = body.getBytes(StandardCharsets.UTF_8).length;

        final String location = content.location();

        final String header = responseHeader.getHeader(httpStatus, bodyLength, location);

        return (header + body).getBytes(StandardCharsets.UTF_8);
    }
}

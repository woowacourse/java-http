package org.apache.coyote.http11.httpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public class HttpResponse {

    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    public HttpResponse(
            final HttpRequest httpRequest
    ) {
        this.responseHeader = new ResponseHeader(httpRequest);
        this.responseBody = new ResponseBody(httpRequest);
    }

    public byte[] getBytes() throws IOException {
        final ResponseContent content = responseBody.getContent();

        final String header = responseHeader.getHeader(content);
        final String body = content.body();

        return (header + body).getBytes(StandardCharsets.UTF_8);
    }
}

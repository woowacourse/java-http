package org.apache.coyote.http11.response;

import java.io.IOException;
import org.apache.coyote.http11.request.RequestUri;

public class ResponseGenerator {

    private final RequestUri requestUri;

    public ResponseGenerator(final RequestUri requestUri) {
        this.requestUri = requestUri;
    }

    public String generateSuccessResponse() throws IOException {
        StringBuilder sb  = new StringBuilder();
        sb.append("HTTP/1.1 200 OK \r\n");
        sb.append("Content-Type: " + requestUri.getContentType() + " \r\n");
        String responseBody = requestUri.getResponseBody();
        int contentLength = responseBody.getBytes().length;
        sb.append("Content-Length: " + contentLength + " \r\n");
        sb.append("\r\n");
        sb.append(responseBody);

        return sb.toString();
    }
}

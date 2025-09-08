package org.apache.coyote.http11.httpResponse;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.HttpStatus;

public class ResponseHeader {

    private final String requestUri;

    public ResponseHeader(
            final String requestUri
    ) {
        this.requestUri = requestUri;
    }

    public String getHeader(
            final HttpStatus httpStatus,
            final int contentLength,
            final String location
    ) {
        final List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 " + httpStatus.toString());
        headers.add("Content-Type: " + getContentType() + ";charset=utf-8");
        headers.add("Content-Length: " + contentLength);

        if (location != null) {
            headers.add("Location: " + location);
        }

        headers.add("\r\n");
        return String.join(" \r\n", headers);
    }

    private String getContentType() {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.equals(".js")) {
            return "text/javascript";
        }
        if (requestUri.equals("svg")) {
            return "image/svg+xml";
        }
        return "text/html";
    }
}

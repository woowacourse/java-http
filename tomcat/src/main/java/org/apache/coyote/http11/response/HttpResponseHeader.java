package org.apache.coyote.http11.response;

import static org.apache.coyote.Constants.CRLF;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.response.element.HttpStatus;

public class HttpResponseHeader {
    private final List<String> headers;

    public HttpResponseHeader(HttpStatus status) {
        this.headers = new ArrayList<>();
        this.headers.add("HTTP/1.1 " + status.getValue() + " ");
    }

    public String getHeaders() {
        return String.join(CRLF, headers);
    }

    public HttpResponseHeader addContentType(String contentType) {
        headers.add("Content-Type: " + contentType + ";charset=utf-8 ");
        return this;
    }

    public HttpResponseHeader addContentLength(int contentLength) {
        headers.add("Content-Length: " + contentLength + " ");
        return this;
    }
}

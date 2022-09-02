package org.apache.coyote.http11.response;

public class HttpResponseHeader {
    private final String headers;
    public HttpResponseHeader(String status) {
        this.headers = toResponseHeaders(status);
    }

    public String getHeaders() {
        return headers;
    }

    private String toResponseHeaders(String status) {
        return String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: text/html;charset=utf-8 ");
    }

    public String getContentLengthHeader(int contentLength) {
        return "Content-Length: " + contentLength + " ";
    }
}

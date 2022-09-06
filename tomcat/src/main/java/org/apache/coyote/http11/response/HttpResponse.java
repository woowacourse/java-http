package org.apache.coyote.http11.response;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final HttpStatus status;
    private final Headers headers;
    private final StringBuilder body;

    public HttpResponse(HttpStatus status, Headers headers, StringBuilder body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse status(HttpStatus status) {
        return new HttpResponse(status, Headers.empty(), new StringBuilder());
    }

    public HttpResponse body(String body) {
        this.body
                .append(body);
        return this;
    }

    public HttpResponse setHeader(String key, Object value) {
        headers.put(key, value.toString());
        return this;
    }

    public byte[] getBytes() {
        return String.join("\r\n",
                HTTP_VERSION + " " + status.code() + " " + status + " ",
                headers.toResponseValue(),
                body).getBytes();
    }
}

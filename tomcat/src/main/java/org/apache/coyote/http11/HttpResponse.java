package org.apache.coyote.http11;

public class HttpResponse {

    private final int statusCode;
    private final String statusText;
    private final String contentType;
    private final byte[] body;

    public HttpResponse(
            int statusCode,
            String statusText,
            String contentType,
            byte[] body
    ) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse ok(String contentType, byte[] body) {
        return new HttpResponse(200, "OK", contentType, body);
    }

    public byte[] toBytes() {
        String body = String.join(
                "\r\n",
                "HTTP/1.1 " + statusCode + " " + statusText,
                "Content-Type: " + contentType,
                "Content-Length: " + this.body.length,
                "",
                "");

        return body.getBytes();
    }
}
package org.apache.coyote.http11;

final class HttpResponse {

    private final String contentType;
    private final String body;

    private HttpResponse(final String contentType, final String body) {
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse ok(final String contentType, final String body) {
        return new HttpResponse(contentType, body);
    }

    public byte[] toBytes() {
        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
        return response.getBytes();
    }
}

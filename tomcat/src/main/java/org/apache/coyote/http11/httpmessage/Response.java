package org.apache.coyote.http11.httpmessage;

public class Response {

    String startLine;
    Headers headers;
    String body;

    public Response(String startLine, Headers headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public Response(String startLine, Headers headers) {
        this.startLine = startLine;
        this.headers = headers;
    }

    public Response() {
    }

    public static Response newInstanceWithResponseBody(final HttpStatus httpStatus, final ContentType contentType,
                                                       final String responseBody) {
        final String startLine = "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getStatus();

        final Headers headers = new Headers()
                .add("Content-Type", contentType.getContentType() + ";charset=utf-8")
                .add("Content-Length", String.valueOf(responseBody.getBytes().length));

        return new Response(startLine, headers, responseBody);
    }

    public static Response of(final HttpStatus httpStatus, final ContentType contentType, final String location) {
        final String startLine = "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getStatus();
        final Headers headers = new Headers()
                .add("Location", location)
                .add("Content-Type", contentType.getContentType() + ";charset=utf-8");

        return new Response(startLine, headers);
    }

    public byte[] getBytes() {
        if (this.body == null) {
            return String.join("\r\n", startLine, headers.parseToString()).getBytes();
        }
        return String.join("\r\n", startLine, headers.parseToString() + "\r\n", body).getBytes();
    }
}

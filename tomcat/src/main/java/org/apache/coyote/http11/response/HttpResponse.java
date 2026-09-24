package org.apache.coyote.http11.response;

public record HttpResponse(
        HttpResponseStatusLine statusLine,
        HttpResponseHeader responseHeader,
        HttpResponseBody responseBody
) {
    private static final String CRLF = "\r\n";

    public static HttpResponse ok() {
        return status(HttpStatus.OK);
    }

    public static HttpResponse found() {
        return status(HttpStatus.FOUND);
    }

    public static HttpResponse status(HttpStatus status) {
        return new HttpResponse(
                HttpResponseStatusLine.from(status),
                HttpResponseHeader.empty(),
                HttpResponseBody.empty()
        );
    }

    public HttpResponse header(String key, String value) {
        return new HttpResponse(statusLine, responseHeader.add(key, value), responseBody);
    }

    public HttpResponse contentType(String contentType) {
        return header("Content-Type", contentType + ";charset=utf-8");
    }

    public HttpResponse location(String location) {
        return header("Location", location);
    }

    public HttpResponse setCookie(String key, String value) {
        return header("Set-Cookie", key + "=" + value);
    }

    public HttpResponse body(String body) {
        return new HttpResponse(statusLine, responseHeader, new HttpResponseBody(body));
    }

    public byte[] getBytes() {
        HttpResponseHeader header = responseHeader.add("Content-Length", String.valueOf(responseBody.length()));

        String message = statusLine.format() + CRLF
                + header.format()
                + CRLF
                + responseBody.format();

        return message.getBytes();
    }
}

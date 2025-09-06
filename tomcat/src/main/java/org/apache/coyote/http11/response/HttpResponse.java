package org.apache.coyote.http11.response;

public record HttpResponse(
        HttpStatus status,
        HttpResponseHeader header,
        String body) {

    public static HttpResponse ok(String body, ContentType contentType) {
        return new HttpResponse(
            HttpStatus.OK,
            HttpResponseHeader.of(contentType, body),
            body
        );
    }

    public static HttpResponse notFound() {
        return new HttpResponse(
            HttpStatus.NOT_FOUND,
            HttpResponseHeader.of(ContentType.TEXT_HTML, "404 Not Found"),
            "404 Not Found"
        );
    }

    public String toHttpResponse() {
        return String.join("\r\n",
                status.getStatusLine() + " ",
                header.toHeaderString(),
                "",
                body);
    }

    public byte[] getBytes() {
        return toHttpResponse().getBytes();
    }
}

package org.apache.coyote.http11;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";

    private final StatusCode statusCode;
    private final String contentType;
    private final String responseBody;

    public HttpResponse(final StatusCode statusCode, final String contentType, final String responseBody) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public byte[] toBytes() {
        return String.join(DELIMITER,
                "HTTP/1.1 " + statusCode.getValue() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody).getBytes();
    }

    public static HttpResponse toNotFound() {
        return new HttpResponse(StatusCode.NOT_FOUND, ContentType.TEXT_HTML.getValue(), ViewLoader.toNotFound());
    }

    public static HttpResponse toUnauthorized() {
        return new HttpResponse(StatusCode.UNAUTHORIZED, ContentType.TEXT_HTML.getValue(), ViewLoader.toUnauthorized());
    }
}

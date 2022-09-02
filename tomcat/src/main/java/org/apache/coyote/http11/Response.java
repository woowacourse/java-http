package org.apache.coyote.http11;

public class Response {

    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";
    private final HttpStatus httpStatus;
    private final String contentType;
    private final String responseBody;

    public Response(final HttpStatus httpStatus, final String contentType, final String responseBody) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public String toHttpResponse() {
        final String statusLine = String.join(" ",
                HTTP_VERSION_1_1 ,
                String.valueOf(httpStatus.getStatusCode()),
                httpStatus.name()) + " ";
        final String contentType = "Content-Type: " + this.contentType + " ";
        final String contentLength = "Content-Length: " + responseBody.getBytes().length + " ";

        return String.join("\r\n",
                statusLine,
                contentType,
                contentLength,
                "", responseBody);
    }
}

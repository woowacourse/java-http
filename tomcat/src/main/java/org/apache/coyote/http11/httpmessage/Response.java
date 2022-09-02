package org.apache.coyote.http11.httpmessage;

public class Response {

    String startLine;
    String headers;
    String body;

    public Response(String startLine, String headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public Response(String startLine, String headers) {
        this.startLine = startLine;
        this.headers = headers;
    }

    public Response() {
    }

    public static Response newInstanceWithResponseBody(HttpStatus httpStatus, String contentType, String responseBody) {
        String startLine = "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getStatus();
        String headers = "Content-Type: " + contentType + ";charset=utf-8 " + "\r\n" +
                "Content-Length: " + responseBody.getBytes().length + " " + "\r\n";

        return new Response(startLine, headers, responseBody);
    }

    public static Response of(HttpStatus httpStatus, String contentType, String location) {
        String startLine = "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getStatus();
        String headers = "Location: " + location + " " + "\r\n" +
                "Content-Type: " + contentType + ";charset=utf-8 ";

        return new Response(startLine, headers);
    }

    public byte[] getBytes() {
        if (this.body == null) {
            return String.join("\r\n", startLine, headers).getBytes();
        }
        return String.join("\r\n", startLine, headers, body).getBytes();
    }
}

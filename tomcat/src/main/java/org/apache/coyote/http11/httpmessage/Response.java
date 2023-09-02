package org.apache.coyote.http11.httpmessage;

public class Response {

    private final String message;

    private Response(String message) {
        this.message = message;
    }

    public static Response from(HttpVersion httpVersion, HttpStatus httpStatus, String contentType,
                                int contentLength, String resource) {
        String message = String.join("\r\n",
                httpVersion + " " + httpStatus + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                resource);

        return new Response(message);
    }

    public byte[] getBytes() {
        return message.getBytes();
    }

    public String getMessage() {
        return message;
    }
}

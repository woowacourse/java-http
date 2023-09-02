package org.apache.coyote.http11;

public class Response {

    private final String message;

    public static Response from(String httpVersion, String httpStatus, String contentType,
                                int contentLength, String resource) {
        String message = String.join("\r\n",
                httpVersion + " " + httpStatus + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                resource);

        System.out.println(message);
        return new Response(message);
    }

    private Response(String message) {
        this.message = message;
    }

    public byte[] getBytes() {
        return message.getBytes();
    }

    public String getMessage() {
        return message;
    }
}

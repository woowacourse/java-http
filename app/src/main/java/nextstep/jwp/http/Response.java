package nextstep.jwp.http;

import java.nio.charset.StandardCharsets;

public class Response {

    private static final String NEW_LINE = "\r\n";
    private static final String CONTENT_LENGTH = "Content-Length: ";

    private final String message;

    private Response(String message) {
        this.message = message;
    }

    public byte[] getBytes() {
        return message.getBytes(StandardCharsets.UTF_8);
    }

    public static Response create200OK(Request request, String responseBody) {
        String message = String.join(NEW_LINE,
            "HTTP/1.1 200 OK ",
            "Content-Type: " + request.acceptType() + ";charset=utf-8 ",
            CONTENT_LENGTH + responseBody.getBytes().length + " ",
            "",
            responseBody);
        return new Response(message);
    }

    public static Response create302Found(String location) {
        String message = String.join(NEW_LINE,
            "HTTP/1.1 302 Found ",
            "Location: " + location
        );
        return new Response(message);
    }

    public static Response create400BadRequest(String errorMessage) {
        String message = String.join(NEW_LINE,
            "HTTP/1.1 400 Bad Request ",
            "",
            errorMessage);
        return new Response(message);
    }

    public static Response create409Conflict(String errorMessage) {
        String message = String.join(NEW_LINE,
            "HTTP/1.1 409 Conflict ",
            "",
            errorMessage);
        return new Response(message);
    }
}

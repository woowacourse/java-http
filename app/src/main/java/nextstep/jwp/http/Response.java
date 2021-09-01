package nextstep.jwp.http;

import java.nio.charset.StandardCharsets;

public class Response {

    private static final String NEW_LINE = "\r\n";
    private static final String CONTENT_LENGTH = "Content-Length: ";

    private final String message;
    private final HttpStatus httpStatus;

    public Response(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public byte[] getBytes() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static Response create200OK(Request request, String responseBody) {
        String message = String.join(NEW_LINE,
            "Content-Type: " + request.acceptType() + ";charset=utf-8",
            CONTENT_LENGTH + responseBody.getBytes().length,
            "",
            responseBody);
        return new Response(message, HttpStatus.OK);
    }

    public static Response create302Found(String location) {
        String message = "Location: " + location;
        return new Response(message, HttpStatus.FOUND);
    }

    public static Response createErrorRequest(String errorMessage, HttpStatus httpStatus) {
        String message = String.join(NEW_LINE,
            "",
            errorMessage);
        return new Response(message, httpStatus);
    }

    @Override
    public String toString() {
        return String.join(NEW_LINE,
            String.format("HTTP/1.1 %d %s", httpStatus.getStatus(), httpStatus.getStatusMessage()),
            message
        );
    }
}

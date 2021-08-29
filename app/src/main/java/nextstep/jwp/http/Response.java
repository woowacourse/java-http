package nextstep.jwp.http;

public class Response {

    private static final String NEW_LINE = "\r\n";
    private static final String CONTENT_LENGTH = "Content-Length: ";

    private final String message;

    private Response(String message) {
        this.message = message;
    }

    public byte[] getBytes() {
        return message.getBytes();
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

    public static Response create302Found(Request request, String location) {
        String message = String.join(NEW_LINE,
            "HTTP/1.1 302 Found ",
            "Location: " + request.getParameter("Host") + location
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

    public static Response create500InternalServerError(String responseBody) {
        String message = String.join(NEW_LINE,
            "HTTP/1.1 500 Internal Server Error",
            "Content-Type: text/html;charset=utf-8 ",
            CONTENT_LENGTH + responseBody.getBytes().length + " ",
            "",
            responseBody);
        return new Response(message);
    }

    public static Response create404NotFound(String responseBody) {
        String message = String.join(NEW_LINE,
            "HTTP/1.1 404 Not Found",
            "Content-Type: text/html;charset=utf-8 ",
            CONTENT_LENGTH + responseBody.getBytes().length + " ",
            "",
            responseBody);
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

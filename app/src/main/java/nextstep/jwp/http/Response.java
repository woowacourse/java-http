package nextstep.jwp.http;

public class Response {

    private static final String NEW_LINE = "\r\n";

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
            "Content-Length: " + responseBody.getBytes().length + " ",
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
}

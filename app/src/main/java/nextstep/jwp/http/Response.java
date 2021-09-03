package nextstep.jwp.http;

public class Response {

    private static final String NEW_LINE = "\r\n";
    private static final String CONTENT_LENGTH = "Content-Length: ";

    private String header;
    private String body;
    private HttpStatus httpStatus;

    public Response() {
        this("", HttpStatus.OK);
    }

    public Response(String header, HttpStatus httpStatus) {
        this(header, "", httpStatus);
    }

    public Response(String header, String body, HttpStatus httpStatus) {
        this.header = header;
        this.body = body;
        this.httpStatus = httpStatus;
    }

    public byte[] getBytes() {
        return this.toString().getBytes();
    }

    public static Response create302Found(String location) {
        String message = "Location: " + location;
        return new Response(message, HttpStatus.FOUND);
    }

    public static Response createErrorRequest(String errorMessage, HttpStatus httpStatus) {
        return new Response("Content-Type: application/json", errorMessage, httpStatus);
    }

    public void set200OK(Request request, String responseBody) {
        header = String.join(NEW_LINE,
            "Content-Type: " + request.acceptType() + ";charset=utf-8",
            CONTENT_LENGTH + responseBody.getBytes().length);
        httpStatus = HttpStatus.OK;
        body = responseBody;
    }

    public void addHeader(String key, String value) {
        header = String.join(NEW_LINE,
            header,
            String.format("%s: %s", key, value));
    }

    public void set302Found(String location) {
        header = "Location: " + location;
        httpStatus = HttpStatus.FOUND;
    }

    @Override
    public String toString() {
        return String.join(NEW_LINE,
            String.format("HTTP/1.1 %d %s", httpStatus.getStatus(), httpStatus.getStatusMessage()),
            header,
            "",
            body
        );
    }
}

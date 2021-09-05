package nextstep.jwp.http;

import java.util.ArrayList;
import java.util.List;

public class Response {

    private static final String NEW_LINE = "\r\n";
    private static final String CONTENT_LENGTH = "Content-Length: ";

    private final List<String> header = new ArrayList<>();
    private String body = "";
    private HttpStatus httpStatus = HttpStatus.OK;

    public byte[] getBytes() {
        return message().getBytes();
    }

    public static Response create302Found(String location) {
        Response response = new Response();
        response.set302Found(location);
        return response;
    }

    public static Response createErrorRequest(String errorMessage, HttpStatus httpStatus) {
        Response response = new Response();
        response.addHeader("Content-Type", "application/json");
        response.body = errorMessage;
        response.httpStatus = httpStatus;
        return response;
    }

    public void set200OK(Request request, String responseBody) {
        header.add("Content-Type: " + request.acceptType() + ";charset=utf-8");
        header.add(CONTENT_LENGTH + responseBody.getBytes().length);
        httpStatus = HttpStatus.OK;
        body = responseBody;
    }

    public void addHeader(String key, String value) {
        header.add(String.format("%s: %s", key, value));
    }

    public void set302Found(String location) {
        header.add("Location: " + location);
        httpStatus = HttpStatus.FOUND;
    }

    public String message() {
        return String.join(NEW_LINE,
            String.format("HTTP/1.1 %d %s", httpStatus.getStatus(), httpStatus.getStatusMessage()),
            String.join(NEW_LINE, header),
            "",
            body
        );
    }
}

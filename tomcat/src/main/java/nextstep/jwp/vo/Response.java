package nextstep.jwp.vo;

import java.util.ArrayList;
import java.util.List;

public class Response {

    private static final String HTTP_1_1 = "HTTP/1.1 ";
    private static final String BLANK = " ";
    private static final String DELIMITER = "\r\n";
    private static final String EMPTY = "";
    private static final String HEADER_DELIMITER = ": ";
    
    private final List<String> headers;

    private Response(ResponseStatus responseStatus) {
        this.headers = new ArrayList<>();
        this.headers.add(HTTP_1_1 + responseStatus.getCode() + BLANK + responseStatus.getStatus() + BLANK);
    }

    public static Response from(ResponseStatus responseStatus) {
        return new Response(responseStatus);
    }

    public Response addHeader(String headerKey, String headerValue) {
        this.headers.add(headerKey + HEADER_DELIMITER + headerValue + BLANK);
        return this;
    }

    public Response addBlankLine() {
        this.headers.add(EMPTY);
        return this;
    }

    public Response addBody(String body) {
        this.headers.add(body);
        return this;
    }

    public String getResponse() {
        return String.join(DELIMITER, this.headers);
    }
}

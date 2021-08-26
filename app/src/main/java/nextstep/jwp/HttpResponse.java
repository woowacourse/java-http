package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String HTTP_LINE_SEPERATOR = "\r\n";
    private HttpStatus status;
    private String body;
    private Map<String, String> headers = new HashMap<>();

    public HttpResponse(HttpStatus status) {
        this(status, "");
    }

    public HttpResponse(HttpStatus status, String body) {
        this.status = status;
        this.body = body;
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public String toString() {
//        return String.join(HTTP_LINE_SEPERATOR,
//            makeStartLine(),
//            "HTTP/1.1 " + status.getCode() + " " + status.getMessage(),
//            "Content-Type: text/html;charset=utf-8 ",
//            "Content-Length: " + body.getBytes().length + " ",
//            "",
//            body);
        return String.join(HTTP_LINE_SEPERATOR,
            makeStartLine(),
            makeHeaderLines(),
            body);
    }

    private String makeStartLine() {
        return "HTTP/1.1 " + status.getCode() + " " + status.getMessage();
    }

    private String makeHeaderLines() {
        StringBuilder sb = new StringBuilder();
        headers.forEach((key, value) -> {
            sb.append(key);
            sb.append(": ");
            sb.append(value);
            sb.append(HTTP_LINE_SEPERATOR);
        });
        return sb.toString();
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }
}

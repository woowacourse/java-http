package nextstep.jwp.web;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final Map<String, String> headers;
    private HttpStatus status;
    private String body;

    public HttpResponse() {
        this.headers = new HashMap<>();
    }

    public HttpResponse status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public HttpResponse contentType(String contentType) {
        this.headers.put("Content-Type", contentType);
        return this;
    }

    public HttpResponse body(String body) {
        this.body = body;
        this.headers.put("Content-Length", Integer.toString(body.getBytes().length));
        return this;
    }

    public String serialize() {
        StringBuilder stringBuilder = new StringBuilder()
                .append(String.join(" ",
                        HTTP_VERSION,
                        Integer.toString(status.getValue()),
                        status.name() + LINE_SEPARATOR));

        serializeHeaders(stringBuilder);
        serializeBody(stringBuilder);

        return stringBuilder.toString();
    }

    private void serializeHeaders(StringBuilder stringBuilder) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            stringBuilder.append(entry.getKey() + ": " + entry.getValue() + LINE_SEPARATOR);
        }
    }

    private void serializeBody(StringBuilder stringBuilder) {
        if (body != null) {
            stringBuilder.append(LINE_SEPARATOR)
                    .append(body);
        }
    }
}

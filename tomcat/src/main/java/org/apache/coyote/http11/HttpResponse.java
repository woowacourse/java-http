package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CRLF = "\r\n";

    private ResponseLine responseLine;
    private final Map<String, String> headers;
    private String body;

    private HttpResponse(ResponseLine responseLine, Map<String, String> headers, String body) {
        this.responseLine = responseLine;
        this.headers = headers;
        setBody(body);
    }

    public static HttpResponse create() {
        return new HttpResponse(
                new ResponseLine(HTTP_VERSION, HttpStatus.OK),
                new LinkedHashMap<>(),
                ""
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(responseLine).append(CRLF);
        headers.forEach((name, value) -> sb.append(name).append(": ").append(value).append(CRLF));
        sb.append(CRLF);
        sb.append(body);
        return sb.toString();
    }

    public void setStatus(HttpStatus status) {
        this.responseLine = this.responseLine.changeStatus(status);
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public void setBody(String body) {
        this.body = body;
        addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void setContentType(ContentType contentType) {
        addHeader("Content-Type", contentType.getValue());
    }
}

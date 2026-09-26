package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String LOCATION_HEADER = "Location";
    private static final String HEADER_SEPARATOR = ": ";
    private static final String CRLF = "\r\n";
    private final Map<String, String> headers;
    private ResponseLine responseLine;
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
        headers.forEach((name, value) -> sb.append(name).append(HEADER_SEPARATOR).append(value).append(CRLF));
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
        addHeader(CONTENT_LENGTH_HEADER, String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void setContentType(ContentType contentType) {
        addHeader(CONTENT_TYPE_HEADER, contentType.getValue());
    }

    public void setLocation(String path) {
        addHeader(LOCATION_HEADER, path);
    }
}

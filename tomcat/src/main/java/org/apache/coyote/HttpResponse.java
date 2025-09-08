package org.apache.coyote;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String HEADER_DELIMITER = ": ";
    private static final String EMPTY_BODY = "";

    private final String protocol;
    private HttpStatus status;
    private String body;
    private HttpHeader headers;

    public HttpResponse(String protocol) {
        this.protocol = protocol;
        initialize();
    }

    public void setBody(String body) {
        this.body = body;
        setContentLength();
    }

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getResponse() {
        return String.join(
                CRLF,
                getStatusLine(),
                getHeaderLine(),
                body
        );
    }

    private String getStatusLine() {
        return String.join(" ", protocol, String.valueOf(status.getValue()), status.getReason());
    }

    private String getHeaderLine() {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : headers.getAllHeaders().entrySet()) {
            for (String headerValue : entry.getValue()) {
                builder.append(entry.getKey()).append(HEADER_DELIMITER).append(headerValue).append(CRLF);
            }
        }
        return builder.toString();
    }

    private void initialize() {
        this.headers = new HttpHeader();
        this.body = EMPTY_BODY;
    }

    private void setContentLength() {
        headers.setContentLength(String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }
}

package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String HEADER_DELIMITER = ": ";

    private HttpStatus status;
    private String protocol;
    private String body;
    private Map<String, String> headers;

    public HttpResponse(String protocol) {
        this.headers = new LinkedHashMap<>();
        this.protocol = protocol;
    }

    public void setBody(String body) {
        this.body = body;
        setContentLength();
    }

    public void setHeader(String name, String value) {
        this.headers.put(name, value);
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
        return String.join(" ", protocol, String.valueOf(status.getValue()), status.name());
    }

    private String getHeaderLine() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append(HEADER_DELIMITER).append(entry.getValue()).append(CRLF);
        }
        return builder.toString();
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }

    private void setContentLength() {
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
    }
}

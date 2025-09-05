package org.apache.coyote;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String HEADER_DELIMITER = ": ";

    private HttpStatus status;
    private final String protocol;
    private String body;
    private final Map<String, String> headers;
    private Charset charset;

    public HttpResponse(String protocol) {
        this.headers = new LinkedHashMap<>();
        this.protocol = protocol;
        this.body = "";
        this.charset = StandardCharsets.UTF_8;
    }

    public void setBody(String body, Charset charset) {
        this.charset = charset;
        setBody(body);
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
        return String.join(" ", protocol, String.valueOf(status.getValue()), status.getReason());
    }

    private String getHeaderLine() {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append(HEADER_DELIMITER).append(entry.getValue()).append(CRLF);
        }
        return builder.toString();
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }

    private void setContentLength() {
        headers.put("Content-Length", String.valueOf(body.getBytes(charset).length));
    }
}

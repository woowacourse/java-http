package org.apache.coyote;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String HEADER_DELIMITER = ": ";

    private final String protocol;
    private HttpStatus status;
    private String body;
    private Map<String, String> headers;
    private Charset charset;

    public HttpResponse(String protocol) {
        this.protocol = protocol;
        initialize();
    }

    public void setBody(String body) {
        this.body = body;
        setContentLength();
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }

    public void sendRedirect(String location){
        initialize();
        status = HttpStatus.FOUND;
        headers.put("Location", location);
        setContentLength();
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

    private void initialize(){
        this.headers = new HashMap<>();
        this.body = "";
        this.charset = StandardCharsets.UTF_8;
    }

    private void setContentLength() {
        headers.put("Content-Length", String.valueOf(body.getBytes(charset).length));
    }
}

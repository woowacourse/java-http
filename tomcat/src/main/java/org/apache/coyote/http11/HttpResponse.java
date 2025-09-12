package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    public static final String LOCATION_HEADER = "Location";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String protocolVersion = DEFAULT_HTTP_VERSION;
    private HttpStatus status = HttpStatus.OK;
    private MimeType mimeType = null;
    private String body = "";

    public void redirect(String location) {
        setStatus(HttpStatus.FOUND);
        addHeader(LOCATION_HEADER, location);
        addHeader(CONTENT_LENGTH_HEADER, String.valueOf(this.body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String toHttpResponseString() {
        StringBuilder sb = new StringBuilder();
        buildHeaders(sb);
        sb.append("\r\n");
        appendBody(sb);
        return sb.toString();
    }

    private void buildHeaders(StringBuilder sb) {
        sb.append(this.protocolVersion)
                .append(" ")
                .append(status.getCodeWithMessage())
                .append("\r\n");

        for (String key : headers.keySet()) {
            sb.append(key)
                    .append(": ")
                    .append(headers.get(key))
                    .append(" ")
                    .append("\r\n");
        }
    }

    private void appendBody(StringBuilder sb) {
        if (!body.isEmpty()) {
            sb.append(this.body);
        }
    }

    public void setResponse(HttpStatus status, MimeType mimeType, String body) {
        this.mimeType = mimeType;
        this.status = status;
        this.body = body;
        if (mimeType != null) {
            headers.put(CONTENT_TYPE_HEADER, mimeType.getType() + ";charset=utf-8");
        }
        if (!body.isEmpty()) {
            headers.put(CONTENT_LENGTH_HEADER, String.valueOf(this.body.getBytes(StandardCharsets.UTF_8).length));
        }
    }

    public void sendResponse(OutputStream outputStream) throws IOException {
        outputStream.write(toHttpResponseString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}

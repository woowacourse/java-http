package org.apache.coyote.util.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String VERSION = "HTTP/1.1";

    private HttpStatus status;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public HttpResponse() {
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void send(OutputStream outputStream) throws IOException {
        outputStream.write(createStatusLine().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        outputStream.write(createHeader().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private String createStatusLine() {
        return VERSION + " " + status.getCode() + " " + status.getMessage() + " \r\n";
    }

    private String createHeader() {
        StringBuilder builder = new StringBuilder();
        if (body.length > 0 && !headers.containsKey("Content-Type")) {
            addHeader("Content-Type", "text/html;charset=utf-8");
        }
        addHeader("Content-Length", String.valueOf(body.length));
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }
        builder.append("\r\n");
        return builder.toString();
    }

    public void setRedirect(String location) {
        setStatus(HttpStatus.FOUND);
        addHeader("Location", location);
    }
}

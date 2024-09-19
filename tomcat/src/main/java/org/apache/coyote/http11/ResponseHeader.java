package org.apache.coyote.http11;

import static org.apache.coyote.http11.Status.FOUND;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeader {

    private final StatusLine statusLine;
    private final Map<String, String> headers = new HashMap<>();
    private String contentType = "";
    private String contentLength = "";
    private String location = "";

    public ResponseHeader(String path, Status status, int bodyLength) {
        this.statusLine = StatusLine.from(status);
        setContentType(path);
        setContentLength(status, bodyLength);
        setLocation(path, status);
    }

    private void setContentLength(Status status, int length) {
        if (FOUND == status) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Content-Length: ")
                .append(length)
                .append(" ")
                .append("\r\n");
        contentLength = stringBuilder.toString();
    }

    private void setContentType(String path) {
        StringBuilder stringBuilder = new StringBuilder();
        String mimeType = "text/html";
        if (path.contains("/css")) {
            mimeType = "text/css";
        }
        stringBuilder.append("Content-Type: ").append(mimeType).append(";")
                .append("charset=utf-8 ")
                .append("\r\n");
        contentType = stringBuilder.toString();
    }

    private void setLocation(String path, Status status) {
        if (status != FOUND) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Location: ")
                .append(path)
                .append(" ")
                .append("\r\n");
        location = stringBuilder.toString();
    }

    public void addHeader(String key, String value) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(key + ": ")
                .append(value)
                .append(" ")
                .append("\r\n");
        String headerValue = stringBuilder.toString();
        headers.put(key, headerValue);
    }

    public String getHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(statusLine.getStatusLine())
                .append("\r\n")
                .append(contentType)
                .append(contentLength)
                .append(location);

        for (String key : headers.keySet()) {
            stringBuilder.append(headers.get(key));
        }
        return stringBuilder.toString();
    }
}

package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HttpHeader {

    private final Map<String, String> header;

    public HttpHeader() {
        this.header = new HashMap<>();
    }

    public void setLocation(String location) {
        addHeader("Location", location);
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void setContentType(MimeType mimeType) {
        addHeader("Content-Type", mimeType.getContentType());
    }

    public boolean hasContentLength() {
        return header.containsKey("Content-Length");
    }

    public String toHeaderString() {
        StringJoiner headerJoiner = new StringJoiner("\r\n");
        for (Map.Entry<String, String> entry : header.entrySet()) {
            headerJoiner.add(entry.getKey() + ": " + entry.getValue() + " ");
        }
        return headerJoiner.toString();
    }

    public int getContentLength() {
        return Integer.parseInt(header.get("Content-Length"));
    }

    public void setContentLength(String contentLength) {
        addHeader("Content-Length", contentLength);
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
                "header=" + header +
                '}';
    }
}

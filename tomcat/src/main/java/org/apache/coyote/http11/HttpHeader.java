package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HttpHeader {

    private final String LOCATION = "Location";
    private final String CONTENT_TYPE = "Content-Type";
    private final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> header;

    public HttpHeader() {
        this.header = new HashMap<>();
    }

    public void setLocation(String location) {
        addHeader(LOCATION, location);
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void setContentType(MimeType mimeType) {
        addHeader(CONTENT_TYPE, mimeType.getContentType());
    }

    public boolean hasContentLength() {
        return header.containsKey(CONTENT_LENGTH);
    }

    public String toHeaderString() {
        StringJoiner headerJoiner = new StringJoiner("\r\n");
        for (Map.Entry<String, String> entry : header.entrySet()) {
            headerJoiner.add(entry.getKey() + ": " + entry.getValue() + " ");
        }
        return headerJoiner.toString();
    }

    public int getContentLength() {
        return Integer.parseInt(header.get(CONTENT_LENGTH));
    }

    public void setContentLength(String contentLength) {
        addHeader(CONTENT_LENGTH, contentLength);
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
                "header=" + header +
                '}';
    }
}

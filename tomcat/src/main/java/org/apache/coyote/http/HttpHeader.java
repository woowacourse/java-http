package org.apache.coyote.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.coyote.http.HttpHeader.HEADER_KEY.CONTENT_LENGTH;
import static org.apache.coyote.http.HttpHeader.HEADER_KEY.CONTENT_TYPE;

public class HttpHeader {

    private final Map<String, List<String>> header;
    private ContentType contentType;
    private int contentLength;

    private HttpHeader(Map<String, List<String>> header, ContentType contentType, int contentLength) {
        this.header = header;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public HttpHeader() {
        this(new HashMap<>(), null, 0);
    }

    public static HttpHeader from(Map<String, List<String>> header) {
        ContentType contentType = readContentType(header);
        int contentLength = readContentLength(header);
        return new HttpHeader(header, contentType, contentLength);
    }

    private static ContentType readContentType(Map<String, List<String>> header) {
        List<String> contentType = header.get(CONTENT_TYPE.value);
        if (contentType == null) {
            return null;
        }
        return ContentType.fromFilePath(contentType.get(0));
    }

    private static int readContentLength(Map<String, List<String>> header) {
        List<String> contentLength = header.get(CONTENT_LENGTH.value);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength.get(0));
    }

    public ContentType getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setValue(String key, String value) {
        List<String> values = header.computeIfAbsent(key, ignored -> new ArrayList<>());
        values.add(value);
    }

    public Map<String, List<String>> getHeader() {
        return new HashMap<>(header);
    }

    public enum HEADER_KEY {

        CONTENT_LENGTH("Content-Length"),
        CONTENT_TYPE("Content-Type"),
        LOCATION("Location");

        public final String value;

        HEADER_KEY(String value) {
            this.value = value;
        }
    }
}

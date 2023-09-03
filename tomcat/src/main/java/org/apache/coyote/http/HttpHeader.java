package org.apache.coyote.http;

import java.util.List;
import java.util.Map;

import static org.apache.coyote.http.HttpHeader.HEADER_KEY.CONTENT_LENGTH;
import static org.apache.coyote.http.HttpHeader.HEADER_KEY.CONTENT_TYPE;

public class HttpHeader {

    private final Map<String, List<String>> header;
    private final ContentType contentType;
    private final int contentLength;

    private HttpHeader(Map<String, List<String>> header, ContentType contentType, int contentLength) {
        this.header = header;
        this.contentType = contentType;
        this.contentLength = contentLength;
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
        return ContentType.from(contentType.get(0));
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

    public enum HEADER_KEY {

        CONTENT_LENGTH("Content-Length"),
        CONTENT_TYPE("Content-Type"),
        ;

        public final String value;

        HEADER_KEY(String value) {
            this.value = value;
        }
    }
}

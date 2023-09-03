package org.apache.coyote.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.coyote.http.HttpHeader.HEADER_KEY.CONTENT_LENGTH;
import static org.apache.coyote.http.HttpHeader.HEADER_KEY.CONTENT_TYPE;

public class HttpHeader {

    private final Map<String, List<String>> header;
    private MediaType mediaType;
    private Charset charset;
    private int contentLength;

    private HttpHeader(Map<String, List<String>> header, MediaType mediaType, Charset charset, int contentLength) {
        this.header = header;
        this.mediaType = mediaType;
        this.charset = charset;
        this.contentLength = contentLength;
    }

    public HttpHeader() {
        this(new HashMap<>(), null, null, 0);
    }

    public static HttpHeader from(Map<String, List<String>> header) {
        String[] mediaTypeAndCharset = readContentType(header);
        MediaType mediaType = null;
        Charset charset = null;
        if (mediaTypeAndCharset != null) {
            mediaType = MediaType.fromValue(mediaTypeAndCharset[0]);
            charset = readCharset(mediaTypeAndCharset);
        }

        int contentLength = readContentLength(header);

        return new HttpHeader(header, mediaType, charset, contentLength);
    }

    private static String[] readContentType(Map<String, List<String>> header) {
        List<String> contentType = header.get(CONTENT_TYPE.value);
        if (contentType == null) {
            return null;
        }

        String contentTypeValue = contentType.get(0);
        return contentTypeValue.split(";");
    }

    private static Charset readCharset(String[] mediaTypeAndCharset) {
        if (mediaTypeAndCharset.length < 2) {
            return null;
        }
        String charset = mediaTypeAndCharset[1];
        if (!charset.substring("charset=".length()).equalsIgnoreCase("utf-8")) {
            throw new IllegalArgumentException("지원하지 않는 인코딩 방식입니다.");
        }
        return StandardCharsets.UTF_8;
    }

    private static int readContentLength(Map<String, List<String>> header) {
        List<String> contentLength = header.get(CONTENT_LENGTH.value);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength.get(0));
    }

    public String getContentType() {
        String contentType = null;
        if (mediaType != null) {
            contentType = mediaType.value;
            if (charset != null) {
                contentType += ";charset=" + charset.name().toLowerCase();
            }
        }

        return contentType;
    }

    public Map<String, List<String>> getHeader() {
        return new HashMap<>(header);
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public Charset getCharset() {
        return charset;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setValue(String key, String value) {
        List<String> values = header.computeIfAbsent(key, ignored -> new ArrayList<>());
        values.add(value);
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpHeader that = (HttpHeader) o;
        return contentLength == that.contentLength && Objects.equals(header, that.header)
            && mediaType == that.mediaType && Objects.equals(charset, that.charset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, mediaType, charset, contentLength);
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
            "header=" + header +
            ", mediaType=" + mediaType +
            ", charset=" + charset +
            ", contentLength=" + contentLength +
            '}';
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

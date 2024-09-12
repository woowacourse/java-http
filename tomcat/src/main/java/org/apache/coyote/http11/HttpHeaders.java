package org.apache.coyote.http11;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String LOCATION = "Location";
    
    private static final String HEADER_DELIMITER = ":";
    private static final int SPLIT_LIMIT = 2;
    private static final int HEADER_FIELD_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> fields = new HashMap<>();
    private final HttpCookies cookies;

    public HttpHeaders(Collection<String> headerLines) {
        headerLines.stream()
                .map(headerLine -> headerLine.split(HEADER_DELIMITER, SPLIT_LIMIT))
                .filter(headerToken -> headerToken.length == SPLIT_LIMIT)
                .forEach(headerToken ->
                        fields.put(headerToken[HEADER_FIELD_INDEX].trim(), headerToken[HEADER_VALUE_INDEX].trim())
                );

        String cookieLine = fields.remove(COOKIE);
        cookies = new HttpCookies(cookieLine);
    }

    public HttpHeaders() {
        this(Collections.emptyList());
    }

    public void put(String name, String value) {
        fields.put(name, value);
    }

    public String get(String name) {
        return fields.get(name);
    }

    public long getContentLength() {
        String contentLength = fields.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return 0L;
        }
        return Long.parseLong(contentLength);
    }

    public Map<String, String> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public HttpCookie getCookie(String name) {
        return cookies.get(name);
    }

    public void setContentType(ContentType contentType) {
        fields.put(CONTENT_TYPE, contentType.getMediaType());
    }

    public void setLocation(String location) {
        fields.put(LOCATION, location);
    }

    public void setContentLength(long contentLength) {
        fields.put(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setCookie(HttpCookie cookie) {
        cookies.add(cookie);
        fields.put(SET_COOKIE, cookie.headerFormat());
    }

    @Override
    public String toString() {
        return fields.toString();
    }
}

package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseHeader {

    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;

    public HttpResponseHeader() {
        this.headers = new HashMap<>();
    }

    public void setLocation(String location) {
        headers.put(LOCATION, location);
    }

    public void setCookie(HttpCookie cookie) {
        headers.put(SET_COOKIE, cookie.toString());
    }

    public void setContentType(HttpContentType contentType) {
        headers.put(CONTENT_TYPE, contentType.getMimeType());
    }

    public void setContentLength(int contentLength) {
        headers.put(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public String getLocation() {
        return headers.get(LOCATION);
    }

    public String getContentType() {
        return headers.get(CONTENT_TYPE);
    }

    public int getContentLength() {
        String contentLength = headers.getOrDefault(CONTENT_LENGTH, "0");
        return Integer.parseInt(contentLength);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

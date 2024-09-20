package org.apache.coyote.http.response;

import static org.apache.coyote.http.HttpHeaderContent.CONTENT_LENGTH;
import static org.apache.coyote.http.HttpHeaderContent.CONTENT_TYPE;
import static org.apache.coyote.http.HttpHeaderContent.LOCATION;
import static org.apache.coyote.http.HttpHeaderContent.SET_COOKIE;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http.HttpContentType;
import org.apache.coyote.http.HttpCookie;

public class HttpResponseHeader {

    private final Map<String, String> headers;

    public HttpResponseHeader() {
        this.headers = new HashMap<>();
    }

    public void setLocation(String location) {
        headers.put(LOCATION.getValue(), location);
    }

    public void setCookie(HttpCookie cookie) {
        headers.put(SET_COOKIE.getValue(), cookie.toString());
    }

    public void setContentType(HttpContentType contentType) {
        headers.put(CONTENT_TYPE.getValue(), contentType.getMimeType());
    }

    public void setContentLength(int contentLength) {
        headers.put(CONTENT_LENGTH.getValue(), String.valueOf(contentLength));
    }

    public int getContentLength() {
        String contentLength = headers.getOrDefault(CONTENT_LENGTH.getValue(), "0");
        return Integer.parseInt(contentLength);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

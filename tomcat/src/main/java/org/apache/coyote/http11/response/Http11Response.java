package org.apache.coyote.http11.response;

import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.ContentType;
import java.util.HashMap;
import java.util.Map;

public class Http11Response {

    private final StatusCode statusCode;
    private final ContentType contentType;
    private final String responseBody;
    private final Map<String, String> otherHeader = new HashMap<>();
    
    private Cookie cookie;

    public Http11Response(final StatusCode statusCode, final ContentType contentType, final String responseBody) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public void addCookie(final String cookie) {
        this.cookie = new Cookie(cookie);
    }

    public boolean containJsessionId() {
        return cookie != null && cookie.containsJsessionId();
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Map<String, String> getOtherHeader() {
        return otherHeader;
    }

    public Cookie getCookie() {
        return cookie;
    }
}

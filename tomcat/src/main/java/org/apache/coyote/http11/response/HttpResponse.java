package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.StatusCode;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final String version;
    private final Map<String, String> otherHeader = new HashMap<>();
    private StatusCode statusCode;
    private ContentType contentType;
    private String redirect;
    private String body;

    private Cookie cookie;

    public HttpResponse(final String version) {
        this(version, null, null, null);
    }

    public HttpResponse(final String version, final StatusCode statusCode, final ContentType contentType, final String redirect) {
        this.version = version;
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.redirect = redirect;
    }

    public void addCookie(final String cookie) {
        this.cookie = new Cookie(cookie);
    }

    public boolean containJsessionId() {
        return cookie != null && cookie.containsJsessionId();
    }

    public boolean containBody() {
        return this.body != null;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Map<String, String> getOtherHeader() {
        return otherHeader;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public String getRedirect() {
        return redirect;
    }

    public String getBody() {
        return body;
    }

    public HttpResponse setStatusCode(final StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse setContentType(final ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponse setRedirect(final String redirect) {
        this.redirect = redirect;
        return this;
    }

    public HttpResponse setBody(final String body) {
        this.body = body;
        return this;
    }
}

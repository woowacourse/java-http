package org.apache.coyote.http11.domain.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.domain.cookie.Cookie;

public class HttpResponseBuilder {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> responseHeaders = new LinkedHashMap<>();
    private HttpStatus httpStatus;
    private String messageBody;

    public HttpResponseBuilder withHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponseBuilder header(String key, String value) {
        this.responseHeaders.put(key, value);
        return this;
    }

    public HttpResponseBuilder contentType(String contentType) {
        this.responseHeaders.put(CONTENT_TYPE, contentType);
        return this;
    }

    public HttpResponseBuilder headers(Map<String, String> headers) {
        this.responseHeaders.putAll(headers);
        return this;
    }

    public HttpResponseBuilder body(String messageBody) {
        this.responseHeaders.put(CONTENT_LENGTH, String.valueOf(messageBody.getBytes().length));
        this.messageBody = messageBody;
        return this;
    }

    public HttpResponseBuilder setCookie(Cookie cookie) {
        header(SET_COOKIE, cookie.toCookieString());
        return this;
    }


    public HttpResponse build() {
        return new HttpResponse(httpStatus, responseHeaders, messageBody);
    }
}

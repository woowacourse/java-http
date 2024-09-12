package org.apache.coyote.http11.protocol.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.protocol.cookie.Cookie;
import org.apache.coyote.http11.protocol.enums.HeaderKey;

public class HttpResponseBuilder {

    private final Map<String, String> responseHeaders = new LinkedHashMap<>();
    private HttpStatus httpStatus;
    private String messageBody;

    public HttpResponseBuilder status(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponseBuilder header(String key, String value) {
        this.responseHeaders.put(key, value);
        return this;
    }

    public HttpResponseBuilder contentType(String contentType) {
        this.responseHeaders.put(HeaderKey.CONTENT_TYPE.getKey(), contentType);
        return this;
    }

    public HttpResponseBuilder headers(Map<String, String> headers) {
        this.responseHeaders.putAll(headers);
        return this;
    }

    public HttpResponseBuilder body(String messageBody) {
        this.responseHeaders.put(HeaderKey.CONTENT_LENGTH.getKey(), String.valueOf(messageBody.getBytes().length));
        this.messageBody = messageBody;
        return this;
    }

    public HttpResponseBuilder setCookie(Cookie cookie) {
        header(HeaderKey.SET_COOKIE.getKey(), cookie.toCookieString());
        return this;
    }


    public HttpResponse build() {
        return new HttpResponse(httpStatus, responseHeaders, messageBody);
    }
}

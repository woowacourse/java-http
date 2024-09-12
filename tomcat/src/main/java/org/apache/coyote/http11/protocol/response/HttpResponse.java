package org.apache.coyote.http11.protocol.response;

import java.util.Map;
import org.apache.coyote.http11.protocol.cookie.Cookie;
import org.apache.coyote.http11.protocol.enums.HeaderKey;

public class HttpResponse {

    private final Map<String, String> responseHeaders;
    private HttpStatus httpStatus;
    private String messageBody;

    HttpResponse(HttpStatus httpStatus, Map<String, String> responseHeaders, String messageBody) {
        this.httpStatus = httpStatus;
        this.responseHeaders = responseHeaders;
        this.messageBody = messageBody;
    }

    public static HttpResponseBuilder status(HttpStatus httpStatus) {
        return new HttpResponseBuilder().status(httpStatus);
    }

    public static HttpResponseBuilder redirect(String location) {
        return status(HttpStatus.FOUND).header(HeaderKey.LOCATION.getKey(), location);
    }

    public static HttpResponse ok() {
        return status(HttpStatus.OK).build();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseHeader(String key) {
        return responseHeaders.get(key);
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String body) {
        this.messageBody = body;
        responseHeaders.put(HeaderKey.CONTENT_LENGTH.getKey(), String.valueOf(body.getBytes().length));
    }

    public void setStatus(HttpStatus status) {
        this.httpStatus = status;
    }

    public void setContentType(String contentType) {
        responseHeaders.put(HeaderKey.CONTENT_TYPE.getKey(), contentType);
    }

    public void setRedirect(String location) {
        this.httpStatus = HttpStatus.FOUND;
        responseHeaders.put(HeaderKey.LOCATION.getKey(), location);
    }

    public void setCookie(Cookie cookie) {
        responseHeaders.put(HeaderKey.SET_COOKIE.getKey(), cookie.toCookieString());
    }
}

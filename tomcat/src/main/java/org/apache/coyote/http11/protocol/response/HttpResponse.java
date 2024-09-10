package org.apache.coyote.http11.protocol.response;

import java.util.Map;
import org.apache.coyote.http11.protocol.cookie.Cookie;

public class HttpResponse {

    private static final String LOCATION_HEADER_KEY = "location";
    private static final String SET_COOKIE_HEADER_KEY = "Set-Cookie";
    private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER_KEY = "Content-Length";

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
        return status(HttpStatus.FOUND).header(LOCATION_HEADER_KEY, location);
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
        responseHeaders.put(CONTENT_LENGTH_HEADER_KEY, String.valueOf(body.getBytes().length));
    }

    public void setStatus(HttpStatus status) {
        this.httpStatus = status;
    }

    public void setContentType(String contentType) {
        responseHeaders.put(CONTENT_TYPE_HEADER_KEY, contentType);
    }

    public void setRedirect(String location) {
        this.httpStatus = HttpStatus.FOUND;
        responseHeaders.put(LOCATION_HEADER_KEY, location);
    }

    public void setCookie(Cookie cookie) {
        responseHeaders.put(SET_COOKIE_HEADER_KEY, cookie.toCookieString());
    }
}

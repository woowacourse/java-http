package org.apache.coyote.support;

import static org.apache.coyote.support.HttpHeader.*;

import java.util.LinkedList;

public class HttpResponse {

    private final LinkedList<String> responseHeaders = new LinkedList<>();
    private boolean hasBody = false;

    private HttpResponse() {
    }

    public static HttpResponse builder() {
        return new HttpResponse();
    }

    public HttpResponse add(HttpHeader header, Object value) {
        final String responseHeader = header.apply(value.toString());
        responseHeaders.add(responseHeader);
        return this;
    }

    public HttpResponse add(HttpHeader header, String value) {
        final String responseHeader = header.apply(value);
        responseHeaders.add(responseHeader);
        return this;
    }

    public HttpResponse add(String header, String value) {
        responseHeaders.add(String.format("%s %s ", header, value));
        return this;
    }

    public HttpResponse addStatus(HttpStatus statusCode) {
        final String responseHeader = HTTP_1_1_STATUS.apply(statusCode);
        responseHeaders.add(responseHeader);
        return this;
    }

    public HttpResponse addCooke() {
        final String responseHeader = HttpHeader.SET_COOKIE.apply(new HttpCookie());
        responseHeaders.add(responseHeader);
        return this;
    }

    /**
     * 아래와 같은 Body를 포함한 문자열이 추가됩니다. <p />
     * "Content-Length: 123<p />
     * <p />
     * This is Body"
     */
    public HttpResponse body(String body) {
        add(HttpHeader.CONTENT_LENGTH, body.getBytes().length);
        responseHeaders.add("");
        responseHeaders.add(body);
        hasBody = true;
        return this;
    }

    public String build() {
        if (!hasBody) {
            responseHeaders.add("");
        }
        return String.join("\r\n", responseHeaders);
    }
}

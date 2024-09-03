package org.apache.coyote.http11.response;

import java.util.Map;

public class HttpResponse {
    private final HttpStatus httpStatus;
    private final Map<String, String> responseHeaders;
    private final String messageBody;

    HttpResponse(HttpStatus httpStatus, Map<String, String> responseHeaders, String messageBody) {
        this.httpStatus = httpStatus;
        this.responseHeaders = responseHeaders;
        this.messageBody = messageBody;
    }

    public static HttpResponseBuilder status(HttpStatus httpStatus) {
        return new HttpResponseBuilder().withHttpStatus(httpStatus);
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
}

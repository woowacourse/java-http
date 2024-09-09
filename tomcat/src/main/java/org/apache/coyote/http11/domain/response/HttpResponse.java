package org.apache.coyote.http11.domain.response;

import java.util.Map;

public class HttpResponse {

    private static final String LOCATION_HEADER_KEY = "location";

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

    public static HttpResponseBuilder redirect(String location) {
        return status(HttpStatus.FOUND).header(LOCATION_HEADER_KEY, location);
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

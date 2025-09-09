package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final List<HttpHeader> headers;
    private final String responseBody;

    public HttpResponse(HttpStatus httpStatus, List<HttpHeader> headers, String responseBody) {
        this.httpStatus = httpStatus;
        this.headers = new ArrayList<>(headers);
        this.responseBody = responseBody;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 %s %s ".formatted(httpStatus.getCode(), httpStatus.getMessage())).append("\r\n");
        for (HttpHeader httpHeader : headers) {
            sb.append("%s: %s ".formatted(httpHeader.getName(), httpHeader.getValue())).append("\r\n");
        }
        sb.append("").append("\r\n");
        sb.append(responseBody);
        return sb.toString();
    }
}

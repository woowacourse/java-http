package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final Map<String, String> headers;
    private final String responseBody;

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers, String responseBody) {
        this.httpStatus = httpStatus;
        this.headers = new HashMap<>(headers);
        this.responseBody = responseBody;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 %s %s ".formatted(httpStatus.getCode(), httpStatus.getMessage())).append("\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append("%s: %s ".formatted(entry.getKey(), entry.getValue())).append("\r\n");
        }
        sb.append("").append("\r\n");
        sb.append(responseBody);
        return sb.toString();
    }
}

package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class HttpResponse {
    private final String httpVersion;
    private HttpStatus httpStatus;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    private HttpResponse(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public static HttpResponse from(String httpVersion) {
        return new HttpResponse(httpVersion);
    }

    public void addHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void addHeader(String headerName, String headerValue) {
        if (headerName.equals("Content-Type") && headerValue.equals("text/html")) {
            headerValue = headerValue + ";charset=utf-8";
        }
        headers.put(headerName, headerValue);
    }

    public void setBody(String body) {
        this.body = body;
        addHeader("Content-Length", String.valueOf(body.getBytes().length));
    }

    public String buildResponse() {
        StringBuilder response = new StringBuilder();
        if (Objects.isNull(httpStatus)) {
            return StringUtils.EMPTY;
        }
        response.append(httpVersion).append(" ").append(httpStatus.getCode()).append(" ").append(httpStatus.name())
                .append("\r\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        response.append("\r\n").append(body);
        return response.toString();
    }
}

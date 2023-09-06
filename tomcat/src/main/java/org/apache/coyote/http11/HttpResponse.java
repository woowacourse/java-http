package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HttpStatus status;
    private final Map<String, String> headers = new HashMap<>();
    private final StringBuilder body = new StringBuilder();

    public String createResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ")
                .append(status.getCode()).append(" ")
                .append(status.getReasonPhrase()).append("\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            responseBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        responseBuilder.append("\r\n");
        responseBuilder.append(body);
        return responseBuilder.toString();
    }

    public void setStatus(HttpStatus httpStatus) {
        this.status = httpStatus;
    }

    public void setCookie(String value) {
        headers.put("Set-Cookie", value);
    }

    public void setContentType(String value) {
        headers.put("Content-Type", value);
    }

    public void setBody(String value) {
        headers.put("Content-Length", String.valueOf(value.getBytes().length));
        body.append(value);
    }

    public void setRedirectUrl(String value) {
        headers.put("Location", value);
    }

}

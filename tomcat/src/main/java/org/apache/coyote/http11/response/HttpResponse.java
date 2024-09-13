package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private HttpStatusCode statusCode;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body;

    public HttpResponse() {
    }

    public String getBody() {
        return body;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setCookie(String cookieKey, String cookieValue) {
        this.headers.put("Set-Cookie", cookieKey + "=" + cookieValue);
    }

    public void setLocation(String location) {
        this.headers.put("Location", location);
    }

    public void setContentType(String contentType) {
        if("text/html".equals(contentType)) {
            this.headers.put("Content-Type", contentType + ";charset=utf-8");
            return;
        }
        this.headers.put("Content-Type", contentType);
    }

    public void setContentLength() {
        this.headers.put("Content-Length", String.valueOf(body.length()));
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append(statusCode)
                .append(System.lineSeparator());

        headers.forEach((key, value) -> stringBuilder
                .append(key)
                .append(": ")
                .append(value)
                .append(System.lineSeparator()));

        stringBuilder
                .append(System.lineSeparator())
                .append(body);

        return stringBuilder.toString();
    }
}

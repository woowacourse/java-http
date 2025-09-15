package org.apache.coyote.dto;

import org.apache.coyote.render.HttpStatus;

public class HttpResponse{
    private String version;
    private int statusCode;
    private HttpHeader headers;
    private String body;

    public HttpResponse() {
    }

    public String toHttpString() {
        StringBuilder response = new StringBuilder();

        response.append(version).append(" ")
                .append(statusCode).append(" ")
                .append(HttpStatus.getMessageByStatusCode(statusCode))
                .append("\r\n");

        if (headers != null) {
            headers.getHeaders().forEach((key, value) ->
                    response.append(key).append(": ").append(value).append("\r\n"));
        }

        response.append("\r\n").append(body);
        return response.toString();
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setHeaders(HttpHeader headers) {
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

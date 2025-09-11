package org.apache.coyote.dto;

import java.util.Map;
import org.apache.coyote.render.HttpStatus;

public class HttpResponse{
    private String version;
    private int statusCode;
    private Map<String,String> headers;
    private String contentType;
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
            headers.forEach((key, value) ->
                    response.append(key).append(": ").append(value).append("\r\n"));
        }

        if (contentType != null && !contentType.isEmpty()) {
            response.append("Content-Type: ").append(contentType).append(";charset=utf-8").append("\r\n");
        }

        response.append("Content-Length: ").append(body.getBytes().length).append("\r\n");
        response.append("\r\n").append(body);
        return response.toString();
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

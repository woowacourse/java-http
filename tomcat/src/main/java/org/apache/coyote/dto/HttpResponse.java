package org.apache.coyote.dto;

import java.util.Map;
import org.apache.coyote.render.HttpStatus;

public class HttpResponse{
    private final String version;
    private final int statusCode;
    private final Map<String,String> headers;
    private final String contentType;
    private final String body;

    public HttpResponse(
            String version,
            int statusCode,
            Map<String, String> headers,
            String contentType,
            String body
    ) {
        this.version = version;
        this.statusCode = statusCode;
        this.headers = headers;
        this.contentType = contentType;
        this.body = body == null ? "" : body;
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
}

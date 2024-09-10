package org.apache.coyote.http11;

public class Http11Response {

    private final String statusCode;
    private final String location;
    private final Http11ResponseContent content;

    public Http11Response(String statusCode, String location) {
        this.statusCode = statusCode;
        this.location = location;
        this.content = null;
    }

    public Http11Response(String statusCode, Http11ResponseContent content) {
        this.statusCode = statusCode;
        this.location = null;
        this.content = content;
    }

    public String serializeResponse() { // TODO: 객체 분리
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(statusCode).append(" \r\n");
        if (location != null) {
            response.append("Location: ").append(location).append(" \r\n");
            response.append("Content-Length: 0 \r\n");
            response.append("\r\n");
        }
        if (content != null) {
            response.append("Content-Type: ").append(content.getContentType()).append(";charset=utf-8 \r\n");
            response.append("Content-Length: ").append(content.getContentLength()).append(" \r\n");
            response.append("\r\n");
            response.append(content.getBody());
        }
        return response.toString();
    }
}

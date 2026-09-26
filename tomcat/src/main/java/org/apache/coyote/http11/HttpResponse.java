package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private String status;
    private String message;
    private String contentType;
    private String responseBody;
    private String location;
    private HttpCookie cookie;

    public HttpResponse() {
        this("200", "OK", null, "", null);
    }

    private HttpResponse(
            String status,
            String message,
            String contentType,
            String responseBody,
            String location
    ) {
        this.status = status;
        this.message = message;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.location = location;
    }

    public static HttpResponse ok(String contentType, String responseBody) {
        return new HttpResponse(
                "200",
                "OK",
                contentType,
                responseBody,
                null
        );
    }

    public static HttpResponse found(String path) {
        return new HttpResponse(
                "302",
                "Found",
                null,
                "",
                path
        );
    }

    public static HttpResponse unsupportedMediaType() {
        return new HttpResponse(
            "415",
                "Unsupported Media Type",
                null,
                "",
                null
        );
    }

    public static HttpResponse badRequest() {
        return new HttpResponse(
                "400",
                "Bad Request",
                null,
                "",
                null
        );
    }

    public void addCookie(HttpCookie cookie) {
        this.cookie = cookie;
    }

    public void setStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String convertString() {
        StringBuilder response = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(status)
                .append(" ")
                .append(message)
                .append(" \r\n");

        if (location != null) {
            response.append("Location: ")
                    .append(location)
                    .append(" \r\n");
        }

        if (cookie != null) {
            response.append("Set-Cookie: ")
                    .append(cookie.toHeaderValue())
                    .append(" \r\n");
        }

        if (contentType != null) {
            response.append("Content-Type: ")
                    .append(contentType)
                    .append(";charset=utf-8 \r\n");
        }

        response.append("Content-Length: ")
                .append(responseBody.getBytes(StandardCharsets.UTF_8).length)
                .append(" \r\n")
                .append("\r\n")
                .append(responseBody);

        return response.toString();
    }
}

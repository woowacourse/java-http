package org.apache.coyote.http11;

public class HttpResponse {
    private StatusCode statusCode;
    private String location;
    private String contentType;
    private String setCookie;
    private int contentLength;
    private String body;

    public HttpResponse() {
        this.statusCode = StatusCode.INTERNAL_SERVER_ERROR;
        this.location = "";
        this.contentType = ResourceHandler.TEXT_HTML_CHARSET_UTF_8;
        this.contentLength = 0;
        this.body = "";
    }

    public byte[] getBytes() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(statusCode.getStatusCode()).append(" ").append(statusCode.getMessage()).append("\r\n");

        if (location != null && !location.isBlank()) {
            sb.append("Location: ").append(location).append("\r\n");
        }
        if (setCookie != null && !setCookie.isBlank()) {
            sb.append("Set-Cookie: ").append(setCookie).append("\r\n");
        }
        sb.append("Content-Type: ").append(contentType).append("\r\n");
        sb.append("Content-Length: ").append(contentLength).append("\r\n");
        sb.append("\r\n");

        sb.append(body != null ? body : "");

        return sb.toString().getBytes();
    }

    public void setCookie(String setCookie) {
        this.setCookie = setCookie;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setBodyAndContentLength(String body) {
        this.body = body;
        this.contentLength = body.getBytes().length;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

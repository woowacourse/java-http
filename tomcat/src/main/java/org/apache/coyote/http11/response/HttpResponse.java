package org.apache.coyote.http11.response;

public class HttpResponse {

    private String protocol = null;
    private HttpStatus status = null;
    private String location = null;
    private Cookie cookie = null;
    private ContentType contentType = null;
    private String responseBody = null;

    public HttpResponse() {
    }

    public HttpResponse addProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public HttpResponse addStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public HttpResponse addLocation(String location) {
        this.location = location;
        return this;
    }

    public HttpResponse addResponseBody(String responseBody, ContentType contentType) {
        this.responseBody = responseBody;
        this.contentType = contentType;
        return this;
    }

    public HttpResponse addCookie(Cookie cookie) {
        this.cookie = cookie;
        return this;
    }

    public String parseToString() {
        StringBuilder sb = new StringBuilder();
        if (protocol != null) {
            sb.append(protocol).append(" ");
        }
        if (status != null) {
            sb.append(status.getCode()).append(" ").append(status.getText()).append(" ");
        }
        sb.append("\r\n");

        if (location != null) {
            sb.append("Location: ").append(location).append("\r\n");
        }
        if (cookie != null) {
            sb.append("Set-Cookie: ").append(cookie.parseToString()).append("\r\n");
        }
        if (responseBody != null) {
            sb.append("Content-Type: ").append(contentType.getType()).append(" ").append("\r\n");
            sb.append("Content-Length: ").append(responseBody.getBytes().length).append(" ").append("\r\n");
            sb.append("\r\n");
            sb.append(responseBody).append(" ").append("\r\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "protocol='" + protocol + '\'' +
                ", status=" + status +
                ", location='" + location + '\'' +
                ", contentType=" + contentType +
                ", responseBody='" + responseBody + '\'' +
                ", cookie='" + cookie + '\'' +
                '}';
    }
}

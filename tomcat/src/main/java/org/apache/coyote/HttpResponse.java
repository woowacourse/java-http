package org.apache.coyote;

public class HttpResponse {

    private final String statusCode;
    private final String location;
    private final HttpResponseContent content;

    public HttpResponse(String statusCode, String location) {
        this.statusCode = statusCode;
        this.location = location;
        this.content = null;
    }

    public HttpResponse(String statusCode, HttpResponseContent content) {
        this.statusCode = statusCode;
        this.location = null;
        this.content = content;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getLocation() {
        return location;
    }

    public HttpResponseContent getContent() {
        return content;
    }

    public String getContentType() {
        return content.getContentType(); // TODO: null 검증 처리
    }

    public int getContentLength() {
        return content.getContentLength();
    }

    public String getBody() {
        return content.getBody();
    }
}

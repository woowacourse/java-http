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

    public String getStatusCode() {
        return statusCode;
    }

    public String getLocation() {
        return location;
    }

    public Http11ResponseContent getContent() {
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

package org.apache.coyote;

import java.util.Map;

public class HttpResponse {

    private final String statusCode;
    private final String location;
    private final HttpResponseHeader header;
    private final HttpResponseContent content;

    public HttpResponse(String statusCode, String location) {
        this.statusCode = statusCode;
        this.location = location;
        this.header = new HttpResponseHeader();
        this.content = null;
    }

    public HttpResponse(String statusCode, HttpResponseContent content) {
        this.statusCode = statusCode;
        this.location = null;
        this.header = new HttpResponseHeader();
        this.content = content;
    }

    public void setCookie(HttpCookie cookie) {
        header.put("Set-Cookie", cookie.toString());
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getLocation() {
        return location;
    }

    public Map<String, String> getHeader() {
        return header.getHeaders();
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

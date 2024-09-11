package org.apache.coyote;

import java.util.Map;

public class HttpResponse {

    private final HttpResponseStartLine startLine;
    private final HttpResponseHeader header;
    private final HttpResponseBody body;

    public HttpResponse(HttpStatusCode statusCode) {
        this.startLine = new HttpResponseStartLine(statusCode);
        this.header = new HttpResponseHeader();
        this.body = new HttpResponseBody();
    }

    public void setLocation(String location) {
        header.setLocation(location);
        header.setContentLength(0);
    }

    public void setCookie(HttpCookie cookie) {
        header.setCookie(cookie);
    }

    public void setContent(String path, String content) {
        header.setContentType(HttpContentType.findByExtension(path));
        header.setContentLength(content.getBytes().length);
        body.setContent(content);
    }

    public String getStatusCode() {
        return startLine.getStatusCode();
    }

    public Map<String, String> getHeader() {
        return header.getHeaders();
    }

    public String getLocation() {
        return header.getLocation();
    }

    public String getContentType() {
        return header.getContentType();
    }

    public int getContentLength() {
        return header.getContentLength();
    }

    public String getBody() {
        return body.getContent();
    }
}

package org.apache.coyote.http.response;

import java.util.Map;
import org.apache.coyote.http.HttpContentType;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpStatusCode;

public class HttpResponse {

    private final HttpResponseStartLine startLine;
    private final HttpResponseHeader header;
    private final HttpResponseBody body;

    public HttpResponse() {
        this.startLine = new HttpResponseStartLine();
        this.header = new HttpResponseHeader();
        this.body = new HttpResponseBody();
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        startLine.setStatusCode(statusCode);
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

    public int getContentLength() {
        return header.getContentLength();
    }

    public String getBody() {
        return body.getContent();
    }
}

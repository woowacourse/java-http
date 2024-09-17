package org.apache.coyote.http.response;

import java.util.Map;
import org.apache.catalina.handler.ViewResolver;
import org.apache.coyote.http.HttpContentType;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpStatusCode;

public class HttpResponse {

    private final HttpResponseStartLine startLine;
    private final HttpResponseHeader header;
    private HttpResponseBody body;

    public HttpResponse() {
        this.startLine = new HttpResponseStartLine();
        this.header = new HttpResponseHeader();
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

    public void setContent(String path) {
        final HttpContentType type = HttpContentType.findByExtension(path);
        final String content = ViewResolver.resolve(path);
        setContent(type, content);
    }

    public void setContent(HttpContentType type, String content) {
        header.setContentType(type);
        header.setContentLength(content.getBytes().length);
        body = new HttpResponseBody(content);
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

    public boolean hasBody() {
        return body != null;
    }

    public String getBody() {
        return body.getContent();
    }
}

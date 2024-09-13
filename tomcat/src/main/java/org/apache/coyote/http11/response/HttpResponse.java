package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.util.StaticFileResponseUtils;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String EMPTY_BODY = "";

    private final HttpResponseHeaders headers;
    private HttpStatus status;
    private String body;

    public HttpResponse(HttpResponseHeaders headers, HttpStatus status, String body) {
        this.headers = headers;
        this.status = status;
        this.body = body;
        headers.addContentLength(body.getBytes().length);
    }

    public HttpResponse() {
        this.headers = new HttpResponseHeaders();
        this.status = HttpStatus.OK;
        this.body = EMPTY_BODY;
        headers.addContentLength(body.getBytes().length);
    }

    public void sendTextFiles(String text) {
        status = HttpStatus.OK;
        body = text;
        headers.addContentType("text/html;charset=utf-8");
        headers.addContentLength(body.getBytes().length);
    }

    public void sendStaticResource(String filePath) {
        sendStaticResource(HttpStatus.OK, filePath);
    }

    public void sendStaticResource(HttpStatus status, String filePath) {
        this.status = status;
        body = StaticFileResponseUtils.readStaticFile(filePath);
        headers.addContentType(StaticFileResponseUtils.getContentType(filePath));
        headers.addContentLength(body.getBytes().length);
    }

    public void sendRedirect(String location) {
        status = HttpStatus.FOUND;
        body = EMPTY_BODY;
        headers.addLocation(location);
        headers.addContentLength(body.getBytes().length);
    }

    public void setSession(Session session) {
        headers.setSession(session);
    }

    public String getHttpVersion() {
        return HTTP_VERSION;
    }

    public int getStatusCode() {
        return status.getStatusCode();
    }

    public String getStatusMessage() {
        return status.getStatusMessage();
    }

    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HttpResponse that = (HttpResponse) object;
        return status == that.status && Objects.equals(headers, that.headers) && Objects.equals(body,
                that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, headers, body);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpResponse.class.getSimpleName() + "[", "]")
                .add("status=" + status)
                .add("headers=" + headers)
                .add("body='" + body + "'")
                .toString();
    }
}

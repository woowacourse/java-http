package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import org.apache.catalina.session.Session;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String EMPTY_BODY = "";

    private final HttpResponseHeaders headers;
    private HttpStatus status;
    private String body;

    public HttpResponse(HttpStatus status, HttpResponseHeaders headers, String body) {
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

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public void sendTextFiles(String text) {
        status = HttpStatus.OK;
        body = text;
        headers.addContentType("text/html");
        headers.addContentLength(body.getBytes().length);
    }

    public void sendStaticFiles(String filePath) {
        status = HttpStatus.OK;
        body = StaticFileResponseUtils.makeResponseBody(filePath);
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

    public static class HttpResponseBuilder {
        private HttpStatus status;
        private Map<String, String> headers;
        private String body;

        private HttpResponseBuilder() {
            this.headers = new HashMap<>();
            this.body = "";
        }

        public HttpResponseBuilder ok() {
            status = HttpStatus.OK;
            return this;
        }

        public HttpResponseBuilder found() {
            status = HttpStatus.FOUND;
            return this;
        }

        public HttpResponseBuilder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public HttpResponseBuilder addHeaders(Map<String, String> addedHeaders) {
            headers.putAll(addedHeaders);
            return this;
        }

        public HttpResponseBuilder contentType(String type) {
            headers.put("Content-Type", type);
            return this;
        }

        public HttpResponseBuilder body(String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            setContentLength();
            return new HttpResponse(status, new HttpResponseHeaders(headers), body);
        }

        private void setContentLength() {
            int contentLength = body.getBytes(StandardCharsets.UTF_8).length;
            headers.put("Content-Length", String.valueOf(contentLength));
        }
    }
}

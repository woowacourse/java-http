package nextstep.jwp.http;

import java.util.List;

public class HttpResponse {
    private final HttpStatus status;
    private final ContentType contentType;
    private final String location;
    private final String responseBody;

    public HttpResponse(final HttpStatus status, final ContentType contentType, final String location, final String responseBody) {
        this.status = status;
        this.contentType = contentType;
        this.location = location;
        this.responseBody = responseBody;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public String getStatus() {
        return status.toString();
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getLocation() {
        return  "Location: " + location;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public static class HttpResponseBuilder {
        private HttpStatus status;
        private ContentType contentType;
        private String location;
        private String responseBody;

        public HttpResponseBuilder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public HttpResponseBuilder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public HttpResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public HttpResponseBuilder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(status, contentType, location, responseBody);
        }
    }

    public String getHttpResponse() {
        String responseStatusLine = "HTTP/1.1 " + getStatus();
        String responseContentType = "Content-Type: " + getContentType().toString() + ";charset=utf-8 ";
        String responseContentLength = "Content-Length: " + getResponseBody().getBytes().length + " ";
        String responseLocation = getLocation();

        return String.join("\r\n",
                responseStatusLine,
                responseContentType,
                responseContentLength,
                responseLocation,
                "",
                responseBody);
    }
}

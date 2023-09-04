package nextstep.jwp;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpResponse {

    private Map<String, String> headers = new LinkedHashMap<>();
    private final HttpStatus httpStatus;
    private final String body;

    public HttpResponse(HttpStatus httpStatus, String body) {
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public HttpResponse(Map<String, String> headers,
                        HttpStatus httpStatus, String body) {
        this.headers = headers;
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public static HttpResponse ok(String responseBody, ContentType contentType) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType.value);
        headers.put("Content-Length", String.valueOf(responseBody.length()));
        return new HttpResponse(headers, HttpStatus.OK, responseBody);
    }

    public static HttpResponse found(String location) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Length", "0");
        headers.put("Location", location);
        return new HttpResponse(headers, HttpStatus.FOUND, "");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getBody() {
        return body;
    }

    public static HttpResponse.Builder builder() {
        return new Builder();
    }

    public static class Builder {

        public Builder() {
        }

        private final Map<String, String> header = new ConcurrentHashMap<>();
        private HttpStatus httpStatus;
        private String body;

        public Builder httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder contentType(ContentType contentType) {
            header.put("Content-Type", contentType.value);
            return this;
        }

        public Builder location(String location) {
            header.put("Location", location);
            return this;
        }

        public Builder body(String body) {
            header.put("Content-Length", String.valueOf(body.getBytes().length));
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(header, httpStatus, body); // Student 생성자 호출
        }
    }
}

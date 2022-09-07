package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    public HttpResponse(Builder builder) {
        this.httpStatus = builder.httpStatus;
        this.responseHeader = new ResponseHeader(builder.headers);
        this.responseBody = new ResponseBody(builder.responseBody);
    }

    public static class Builder{

        private HttpStatus httpStatus;
        private Map<String, String> headers;
        private String responseBody;

        public Builder() {
            this.headers = new LinkedHashMap<>();
        }

        public Builder status(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder responseBody(String responseBody) {
            final int contentLength = responseBody.getBytes().length;
            headers.put("Content-Length", String.valueOf(contentLength));

            this.responseBody = responseBody;
            return this;
        }

        public Builder contentType(String contentType) {
            headers.put("Content-Type", contentType + ";charset=utf-8");
            return this;
        }

        public Builder location(String path) {
            headers.put("Location", path);
            return this;
        }

        public Builder cookie(String sessionId) {
            headers.put("Set-Cookie", sessionId);
            return this;
        }

        public Builder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }

    public String toResponseMessage() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getStatus() + " ",
                responseHeader.toHeaderFieldMessage(),
                responseBody.getValue()
                );
    }
}

package org.apache.coyote.http11.model.response;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;

public class HttpResponse {

    private static final String JSESSIONID_HEADER_PREFIX = "JSESSIONID=";

    private final HttpStatusLine statusLine;
    private final HttpResponseHeader headers;
    private final String responseBody;

    public HttpResponse(Builder builder) {
        this.statusLine = new HttpStatusLine(builder.statusCode);
        this.headers = new HttpResponseHeader(builder.headers);
        this.responseBody = builder.responseBody;
    }

    public static class Builder {
        private HttpStatus statusCode;
        private Map<String, String> headers;

        private String responseBody;

        public Builder() {
            this.headers = new LinkedHashMap<>();
        }

        public Builder statusCode(HttpStatus statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder header(String headerName, String headerValue) {
            this.headers.put(headerName, headerValue);
            return this;
        }

        public Builder addCookie(String cookie) {
            this.headers.put(HttpHeaderType.SET_COOKIE, JSESSIONID_HEADER_PREFIX + cookie);
            return this;
        }

        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            int contentLength = responseBody.getBytes().length;
            this.headers.put(HttpHeaderType.CONTENT_LENGTH, String.valueOf(contentLength));
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }

    public static HttpResponse redirect(String redirectUrl) {
        return new HttpResponse.Builder()
                .statusCode(HttpStatus.FOUND)
                .header(HttpHeaderType.LOCATION, redirectUrl)
                .build();
    }

    public static HttpResponse notFound() {
        return new HttpResponse.Builder()
                .statusCode(HttpStatus.NOT_FOUND)
                .build();
    }

    public byte[] toResponse() {
        if (responseBody == null) {
            return String.join("\r\n",
                    statusLine.toResponse(),
                    headers.toResponse()).getBytes();
        }
        return String.join("\r\n",
                statusLine.toResponse(),
                headers.toResponse(),
                responseBody).getBytes();
    }

    public String getProtocolVersion() {
        return statusLine.getProtocolVersion();
    }

    public HttpStatus getStatusCode() {
        return statusLine.getHttpStatus();
    }

    public String getHeader(String headerName) {
        return headers.getHeader(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }
}

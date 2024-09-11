package org.apache.coyote.http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final HttpVersion httpVersion;
    private final StatusCode statusCode;
    private final Map<String, String> headers;
    private final String responseBody;

    private HttpResponse(String httpVersion, String statusCode, Map<String, String> headers, String responseBody) {
        this.httpVersion = new HttpVersion(httpVersion);
        this.statusCode = new StatusCode(statusCode);
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder().withHttpVersion("HTTP/1.1");
    }

    public static class HttpResponseBuilder {
        private String httpVersion;
        private String statusCode;
        private Map<String, String> headers = new HashMap<>();
        private String responseBody;

        public HttpResponseBuilder withHttpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public HttpResponseBuilder withStatusCode(String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public HttpResponseBuilder addHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public HttpResponseBuilder withResponseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(httpVersion, statusCode, headers, responseBody);
        }
    }

    public byte[] getBytes() {
        StringBuilder responseBuilder = new StringBuilder();

        responseBuilder.append(httpVersion.version()).append(" ")
                .append(statusCode.statusCode()).append(" ").append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseBuilder.append(header.getKey()).append(": ").append(header.getValue()).append(" ").append("\r\n");
        }

        responseBuilder.append("\r\n");

        if (responseBody != null && !responseBody.isEmpty()) {
            responseBuilder.append(responseBody);
        }

        return responseBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
}

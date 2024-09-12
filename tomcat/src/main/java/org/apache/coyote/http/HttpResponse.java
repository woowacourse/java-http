package org.apache.coyote.http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String LINE_BREAK = "\r\n";
    private static final String SPACE = " ";
    private static final String HEADER_SEPARATOR = ": ";

    private final HttpVersion httpVersion;
    private final StatusCode statusCode;
    private final Map<String, String> headers;
    private final String responseBody;

    private HttpResponse(HttpVersion httpVersion, StatusCode statusCode, Map<String, String> headers, String responseBody) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder().withHttpVersion(HttpVersion.HTTP_1_1);
    }

    public static class HttpResponseBuilder {
        private HttpVersion httpVersion;
        private StatusCode statusCode;
        private Map<String, String> headers = new HashMap<>();
        private String responseBody;

        public HttpResponseBuilder withHttpVersion(HttpVersion httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public HttpResponseBuilder withStatusCode(StatusCode statusCode) {
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

        appendStatusLine(responseBuilder, httpVersion, statusCode);
        appendHeaders(responseBuilder, headers);
        appendBody(responseBuilder, responseBody);

        return responseBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void appendStatusLine(StringBuilder responseBuilder, HttpVersion httpVersion, StatusCode statusCode) {
        responseBuilder.append(httpVersion.getVersion())
                .append(SPACE)
                .append(statusCode.getStatusCode())
                .append(SPACE)
                .append(LINE_BREAK);
    }

    private void appendHeaders(StringBuilder responseBuilder, Map<String, String> headers) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseBuilder.append(header.getKey())
                    .append(HEADER_SEPARATOR)
                    .append(header.getValue())
                    .append(SPACE)
                    .append(LINE_BREAK);
        }
        responseBuilder.append(LINE_BREAK);
    }

    private void appendBody(StringBuilder responseBuilder, String responseBody) {
        if (responseBody != null && !responseBody.isEmpty()) {
            responseBuilder.append(responseBody);
        }
    }
}

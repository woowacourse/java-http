package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private final String protocolVersion;
    private final HttpStatus statusCode;
    private final Map<String, String> headers;
    private final String responseBody;

    public static class Builder {

        private String protocolVersion;
        private HttpStatus statusCode;
        private Map<String, String> headers;
        private String responseBody;

        public Builder() {
            this.protocolVersion = PROTOCOL_VERSION;
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

    public HttpResponse(Builder builder) {
        this.protocolVersion = builder.protocolVersion;
        this.statusCode = builder.statusCode;
        this.headers = builder.headers;
        this.responseBody = builder.responseBody;
    }

    public byte[] toResponse() {
        return String.join("\r\n",
                protocolVersion + " " + statusCode.toResponseMessage() + " ",
                headersToResponse(),
                responseBody).getBytes();
    }

    private String headersToResponse() {
        StringBuilder headerResponse = new StringBuilder();
        for (String headerName : headers.keySet()) {
            headerResponse.append(headerName)
                    .append(": ")
                    .append(headers.get(headerName))
                    .append(" \r\n");
        }
        return headerResponse.toString();
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }
}

package org.apache.coyote.http11.model.response;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private final String protocolVersion;
    private final HttpStatus statusCode;
    private final Map<String, String> headers;
    private final String responseBody;

    public HttpResponse(Builder builder) {
        this.protocolVersion = builder.protocolVersion;
        this.statusCode = builder.statusCode;
        this.headers = builder.headers;
        this.responseBody = builder.responseBody;
    }

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
                    protocolVersion + " " + statusCode.toResponseMessage() + " ",
                    headersToResponse()).getBytes();
        }
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

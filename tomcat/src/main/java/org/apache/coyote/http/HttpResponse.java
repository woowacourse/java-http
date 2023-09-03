package org.apache.coyote.http;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final StatusCode statusCode;
    private final HttpMessage httpMessage;
    private final String location;

    private HttpResponse(StatusCode statusCode, HttpMessage httpMessage, String location) {
        if (statusCode == null) {
            throw new IllegalArgumentException(); 
        }
        this.statusCode = statusCode;
        this.httpMessage = httpMessage;
        this.location = location;
    }

    public byte[] getBytes() {
        String response = "HTTP/1.1 " + statusCode.value + " " + statusCode.name() + "\r\n";
        if (location != null) {
            response += "Location: " + location + "\r\n";
        }
        if (httpMessage != null) {
            byte[] contentBytes = httpMessage.getContent().getBytes(StandardCharsets.UTF_8);
            response += "Content-Length: " + contentBytes.length + " \r\n";
            response += "Content-Type: " + httpMessage.getContentType().value + " \r\n";
            response += "\r\n" + httpMessage.getContent();
        }

        return response.getBytes();
    }

    public static class Builder {

        private StatusCode statusCode;
        private HttpMessage httpMessage;
        private String location;

        public Builder statusCode(StatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder httpMessage(HttpMessage httpMessage) {
            this.httpMessage = httpMessage;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusCode, httpMessage, location);
        }
    }
}

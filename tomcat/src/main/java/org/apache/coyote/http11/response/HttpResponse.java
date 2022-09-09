package org.apache.coyote.http11.response;

import java.util.Map;

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private Status status;
    private ResponseHeaders responseHeaders;
    private String body;

    public HttpResponse() {
        this.responseHeaders = new ResponseHeaders();
    }

    private HttpResponse(final Status status, final ResponseHeaders responseHeaders, final String body) {
        this.status = status;
        this.responseHeaders = responseHeaders;
        this.body = body;
    }

    public String toMessage() {
        return String.join("\r\n", PROTOCOL_VERSION + " " + status.getCode() + " " + status.getMessage() + " ",
                responseHeaders.toMessage(), "", body);
    }

    public byte[] toBytes() {
        return toMessage().getBytes();
    }

    public void addCookie(String cookie) {
        responseHeaders.setCookie("JSESSIONID=" + cookie);
    }

    public void setHeaders(final Map<String, String> headers) {
        this.responseHeaders = new ResponseHeaders();
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public Status getStatus() {
        return status;
    }

    public ResponseHeaders getHeaders() {
        return responseHeaders;
    }

    public String getBody() {
        return body;
    }

    public HttpResponse redirect(final String redirectUrl) {
        this.status = Status.FOUND;
        this.responseHeaders.setLocation(redirectUrl);
        return this;
    }

    public static class ResponseBuilder {
        private Status status;
        private ResponseHeaders responseHeaders = new ResponseHeaders();
        private String body = "";

        public ResponseBuilder status(final Status status) {
            this.status = status;
            return this;
        }

        public ResponseBuilder headers(final ResponseHeaders responseHeaders) {
            this.responseHeaders = responseHeaders;
            return this;
        }

        public ResponseBuilder body(final String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(status, responseHeaders, body);
        }
    }
}


package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public HttpResponse create200Response(final Map<String, String> headers, final String body) {
        this.setHeaders(headers);
        this.setBody(body);

        return this;
    }

    public HttpResponse create302Response(final Map<String, String> headers, final String body) {
        this.setHeaders(headers);
        this.setBody(body);

        return this;
    }

    public HttpResponse create401Response() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final Path path = new File(resource.getFile()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));

        return this.create200Response(Map.of("Content-Type", Files.probeContentType(path)), responseBody);
    }

    public String toMessage() {
        return String.join("\r\n",
                PROTOCOL_VERSION + " " + status.getCode() + " " + status.getMessage() + " ",
                responseHeaders.toMessage(),
                "",
                body
        );
    }

    public byte[] toBytes() {
        return toMessage().getBytes();
    }

    public void addCookie(String cookie) {
        responseHeaders.setCookie(cookie);
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


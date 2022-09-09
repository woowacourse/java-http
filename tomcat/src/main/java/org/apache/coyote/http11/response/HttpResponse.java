package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class HttpResponse {
    private final String protocolVersion = "HTTP/1.1";

    private Status status;
    private Headers headers;
    private String body;

    public HttpResponse() {
        this.headers = new Headers();
    }

    private HttpResponse(final Status status, final Headers headers, final String body) {
        this.status = status;
        this.headers = headers;
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
                protocolVersion + " " + status.getCode() + " " + status.getMessage() + " ",
                headers.toMessage(),
                "",
                body
        );
    }

    public byte[] toBytes() {
        return toMessage().getBytes();
    }

    public void addCookie(String cookie) {
        headers.setCookie(cookie);
    }

    public void setHeaders(final Map<String, String> headers) {
        this.headers = new Headers();
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public static class ResponseBuilder {
        private Status status;
        private Headers headers;
        private String body;

        public ResponseBuilder status(final Status status) {
            this.status = status;
            return this;
        }

        public ResponseBuilder headers(final Headers headers) {
            this.headers = headers;
            return this;
        }

        public ResponseBuilder body(final String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(status, headers, body);
        }
    }
}


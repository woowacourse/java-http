package org.apache.coyote;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HttpFormTestUtils {

    private final StringBuilder sb;

    private HttpFormTestUtils(final StringBuilder sb) {
        this.sb = sb;
    }

    public static HttpFormTestUtils builder() {
        return new HttpFormTestUtils(new StringBuilder());
    }

    public HttpFormTestUtils http11() {
        sb.append("HTTP/1.1 ");
        return this;
    }

    public HttpFormTestUtils GET() {
        sb.append("GET ");
        return this;
    }

    public HttpFormTestUtils POST() {
        sb.append("POST ");
        return this;
    }

    public HttpFormTestUtils requestUri(final String uri) {
        sb.append(uri).append(" ");
        return this;
    }

    public HttpFormTestUtils OK() {
        sb.append("200 OK ");
        return this;
    }

    public HttpFormTestUtils FOUND() {
        sb.append("302 FOUND ");
        return this;
    }

    public HttpFormTestUtils host(final String host) {
        sb.append("Host: ").append(host).append(" ");
        return this;
    }

    public HttpFormTestUtils connection(final String connection) {
        sb.append("Connection: ").append(connection).append(" ");
        return this;
    }

    public HttpFormTestUtils enter() {
        sb.append("\r\n");
        return this;
    }

    public HttpFormTestUtils accept(final String accept) {
        sb.append("Accept: ").append(accept).append(" ");
        return this;
    }

    public HttpFormTestUtils contentLength(final String length) {
        sb.append("Content-Length: ").append(length).append(" ");
        return this;
    }

    public HttpFormTestUtils contentType(final String type) {
        sb.append("Content-Type: ").append(type).append(" ");
        return this;
    }

    public HttpFormTestUtils location(final String uri) {
        sb.append("Location: ").append(uri).append(" ");
        return this;
    }

    public HttpFormTestUtils responseByResource(final String name) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(name);
        sb.append(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
        return this;
    }

    public HttpFormTestUtils requestBody(final String body) {
        sb.append(body);
        return this;
    }

    public String build() {
        return sb.toString();
    }
}

package org.apache.coyote.http11.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringJoiner;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String protocol;
    private final Headers headers;
    private final String body;

    public HttpRequest(final InputStream inputStream) throws IOException {
        final var reader = new InputStreamReader(inputStream);
        final var buffer = new BufferedReader(reader);
        final var requestLine = buffer.readLine();
        final var requestLineSplit = requestLine.split(" ");
        this.method = requestLineSplit[0];
        this.path = requestLineSplit[1];
        this.protocol = requestLineSplit[2];
        this.headers = createHeaders(buffer);
        this.body = createBody(buffer);
    }

    private Headers createHeaders(final BufferedReader reader) throws IOException {
        final var headers = new Headers();
        var line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            headers.put(line);
            line = reader.readLine();
        }
        return headers;
    }

    private String createBody(final BufferedReader reader) throws IOException {
        final var body = new StringJoiner("\r\n");
        var line = reader.readLine();
        while (line != null) {
            body.add(line);
            line = reader.readLine();
        }
        return body.toString();
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}

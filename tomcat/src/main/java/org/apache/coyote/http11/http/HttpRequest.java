package org.apache.coyote.http11.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringJoiner;

public class HttpRequest {

    private final String method;
    private final String requestURI;
    private final String httpVersion;
    private final Headers headers;
    private final String body;

    public HttpRequest(final InputStream inputStream) throws IOException {
        final var reader = new InputStreamReader(inputStream);
        final var buffer = new BufferedReader(reader);
        final var requestLine = buffer.readLine();
        final var requestLineSplit = requestLine.split(" ");
        this.method = requestLineSplit[0];
        this.requestURI = requestLineSplit[1];
        this.httpVersion = requestLineSplit[2];
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
        while (reader.ready()) {
            var line = reader.readLine();
            body.add(line);
        }
        return body.toString();
    }

    public String getPath() {
        int index = requestURI.lastIndexOf("?");
        if (index == -1) {
            return requestURI;
        }
        return requestURI.substring(0, index);
    }

    public String getQueryString() {
        int index = requestURI.lastIndexOf("?");
        if (index == -1) {
            return "";
        }
        return requestURI.substring(index + 1);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getAccept() {
        return headers.get("Accept");
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        final var result = new StringJoiner("\r\n");
        final var requestLine = method + " " + requestURI + " " + httpVersion + " ";
        return result.add(requestLine)
                .add(headers.toString())
                .add(body)
                .toString();
    }
}

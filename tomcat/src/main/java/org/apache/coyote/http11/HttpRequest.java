package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestParameters parameters;
    private final String body;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders headers,
                        final RequestParameters parameters, final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.parameters = parameters;
        this.body = body;
    }

    public static Optional<HttpRequest> from(final BufferedReader reader) throws IOException {
        final String startLine = reader.readLine();
        if (startLine == null) {
            return Optional.empty();
        }
        final RequestLine requestLine = RequestLine.from(startLine);
        final RequestHeaders headers = RequestHeaders.from(readHeaderLines(reader));
        final String body = readBody(reader, headers.getContentLength());
        final RequestParameters parameters = RequestParameters.of(
                requestLine.getQueryString(), headers.getHeader("Content-Type"), body);

        return Optional.of(new HttpRequest(requestLine, headers, parameters, body));
    }

    private static List<String> readHeaderLines(final BufferedReader reader) throws IOException {
        final List<String> headerLines = new ArrayList<>();
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            headerLines.add(line);
            line = reader.readLine();
        }
        return headerLines;
    }

    private static String readBody(final BufferedReader reader, final int contentLength) throws IOException {
        final char[] buffer = new char[contentLength];
        int total = 0;
        while (total < buffer.length) {
            final int read = reader.read(buffer, total, buffer.length - total);
            if (read == -1) {
                break;
            }
            total += read;
        }
        return new String(buffer, 0, total);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(headers.getHeader("Cookie"));
    }

    public String getParameter(final String name) {
        return parameters.getParameter(name);
    }

    public String getHeader(final String name) {
        return headers.getHeader(name);
    }

    public String getBody() {
        return body;
    }
}

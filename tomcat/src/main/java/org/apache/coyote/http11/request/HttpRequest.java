package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpParser;

public class HttpRequest {

    private final RequestStartLine startLine;
    private final RequestHeader header;
    private final String body;

    public HttpRequest(final RequestStartLine startLine, final RequestHeader header, final String body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final var startLine = RequestStartLine.from(bufferedReader.readLine());
        final var header = RequestHeader.from(readHeader(bufferedReader));
        final var body = readBody(bufferedReader, header);
        return new HttpRequest(startLine, header, body);
    }

    private static String readHeader(final BufferedReader bufferedReader) throws IOException {
        final var stringBuilder = new StringBuilder();
        var line = "";
        while (!(line = bufferedReader.readLine()).isBlank()) {
            stringBuilder.append(line).append("\r\n");
        }
        return stringBuilder.toString();
    }

    private static String readBody(final BufferedReader bufferedReader, final RequestHeader header) throws IOException {
        final var contentLength = parseContentLength(header);
        final var buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private static int parseContentLength(final RequestHeader header) {
        final var contentLength = header.get("Content-Length");
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public Map<String, String> parseQueryString() {
        return HttpParser.parseQueryString(getQueryString());
    }

    public Map<String, String> parseBodyQueryString() {
        return HttpParser.parseQueryString(getBody());
    }

    public boolean isSameMethod(final HttpMethod httpMethod) {
        return this.startLine.isSameMethod(httpMethod);
    }

    public RequestStartLine getStartLine() {
        return startLine;
    }

    public String toStartLineString() {
        return startLine.toString();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }

    public String getQueryString() {
        return startLine.getQueryString();
    }

    public RequestHeader getHeader() {
        return header;
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(header.get("Cookie"));
    }

    public Session getSession() {
        return new Session(getCookie().getSessionId());
    }

    public String getBody() {
        return body;
    }
}

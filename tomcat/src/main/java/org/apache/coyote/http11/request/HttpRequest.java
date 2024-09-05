package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeaders;

public class HttpRequest {

    private final HttpRequestLine startLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(HttpRequestLine startLine, HttpHeaders headers, HttpBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        HttpRequestLine httpRequestLine = new HttpRequestLine(parseStartLine(reader));
        HttpHeaders httpHeaders = new HttpHeaders(parseHeaders(reader));
        HttpBody httpBody = new HttpBody(parseBody(reader, httpHeaders.getContentLength()));

        return new HttpRequest(httpRequestLine, httpHeaders, httpBody);
    }

    private static String parseStartLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private static String parseHeaders(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        while (StringUtils.isNoneBlank(line = reader.readLine())) {
            builder.append(line).append(System.lineSeparator());
        }
        return builder.toString();
    }

    private static String parseBody(BufferedReader reader, int countLength) throws IOException {
        char[] buffer = new char[countLength];
        reader.read(buffer, 0, countLength);
        return new String(buffer);
    }

    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    public String getBody() {
        return body.getBody();
    }

    public String getPath() {
        return startLine.getUri();
    }

    public HttpRequestLine getStartLine() {
        return startLine;
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }
}

package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Http11Request {

    private final StartLine startLine;
    private final Http11RequestHeaders headers;
    private final Http11RequestBody body;

    private Http11Request(
            final StartLine startLine,
            final Http11RequestHeaders headers,
            final Http11RequestBody body
    ) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static Http11Request from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = extractRequestHeadersWithStartLine(reader);

        StartLine startLine = StartLine.extractStartLine(lines.getFirst());
        Http11RequestHeaders headers = Http11RequestHeaders.extractHeaders(lines);
        Http11RequestBody body = Http11RequestBody.from(reader, headers);

        return new Http11Request(startLine, headers, body);
    }

    private static List<String> extractRequestHeadersWithStartLine(final BufferedReader bufferedReader)
            throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        String requestLine;
        while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isEmpty()) {
            requestHeaders.add(requestLine);
        }

        return requestHeaders;
    }

    public String getSessionId() {
        return headers.getSessionId();
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return startLine.isSameHttpMethod(httpMethod);
    }

    public Map<String, String> extractRequestBodyParams() {
        return body.parseFormParams();
    }

    public String getUri() {
        return startLine.getUri();
    }

    public HttpMethod getHttpMethod() {
        return startLine.getHttpMethod();
    }
}

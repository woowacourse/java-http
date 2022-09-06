package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {
    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    private HttpRequest(HttpRequestStartLine startLine, HttpRequestHeader header,
                        HttpRequestBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        final String startLine = reader.readLine();
        if (startLine == null) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        String header;
        final Map<String, String> headers = new HashMap<>();
        while (!"".equals(header = reader.readLine())) {
            final String[] keyAndValue = header.split(": ");
            headers.put(keyAndValue[0], keyAndValue[1]);
        }

        String bodyLine = null;
        if (reader.ready()) {
            bodyLine = reader.readLine();
        }

        return new HttpRequest(HttpRequestStartLine.from(startLine),
                new HttpRequestHeader(headers),
                new HttpRequestBody(getRequestBody(bodyLine)));
    }

    private static Map<String, String> getRequestBody(final String bodyLine) {
        final Map<String, String> body = new HashMap<>();

        if (bodyLine == null) {
            return body;
        }

        final String[] params = bodyLine.split("&");
        for (String param : params) {
            final String[] keyValue = param.split("=");
            body.put(keyValue[0], keyValue[1]);
        }
        return body;
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public HttpRequestHeader getHeader() {
        return header;
    }

    public HttpRequestBody getBody() {
        return body;
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getUri() {
        return startLine.getUri();
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "startLine=" + startLine +
                ", header=" + header +
                ", body=" + body +
                '}';
    }
}

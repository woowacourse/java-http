package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> header;
    private final Map<String, String> body;

    private HttpRequest(final RequestLine requestLine, final Map<String, String> header, final Map<String, String> body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest of(final InputStream inputStream) {
        final HttpRequestParser httpRequestParser = new HttpRequestParser(inputStream);
        try {
            final RequestLine requestLine = httpRequestParser.parseRequestLine();
            final Map<String, String> header = httpRequestParser.parseRequestHeader();
            if (header.get("Content-Length") != null) {
                final Map<String, String> body = httpRequestParser.parseRequestBody(header.get("Content-Length"));
                return new HttpRequest(requestLine, header, body);
            }
            return new HttpRequest(requestLine, header, new HashMap<>());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getTarget() {
        return requestLine.getTarget();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Map<String, String> getBody() {
        return body;
    }
}

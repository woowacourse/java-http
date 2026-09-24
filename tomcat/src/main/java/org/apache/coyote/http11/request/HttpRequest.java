package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final RequestBody body;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        final HttpHeaders headers = HttpHeaders.from(reader);
        final RequestBody body = RequestBody.of(reader, headers);
        if (line == null) {
            return new HttpRequest(null, headers, body);
        }
        return new HttpRequest(RequestLine.from(line), headers, body);
    }

    public boolean isEmpty() {
        return requestLine == null;
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpCookie getCookie() {
        return headers.getCookie();
    }

    public Map<String, String> getBodyParams() {
        return body.toParams();
    }
}
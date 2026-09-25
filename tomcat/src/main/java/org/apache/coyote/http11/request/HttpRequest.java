package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final RequestBody body;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static Optional<HttpRequest> from(final BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        if (line == null) {
            return Optional.empty();
        }
        final RequestLine requestLine = RequestLine.from(line);
        final HttpHeaders headers = HttpHeaders.from(reader);
        final RequestBody body = RequestBody.of(reader, headers);
        return Optional.of(new HttpRequest(requestLine, headers, body));
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

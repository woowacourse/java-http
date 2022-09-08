package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Cookies;
import org.apache.catalina.Session;

public class Http11Request {

    private final RequestLine requestLine;
    private final HttpRequestHeaders httpRequestHeaders;
    private final String requestBody;
    private final Cookies cookies;

    private Http11Request(final RequestLine requestLine, final HttpRequestHeaders httpHeaders,
                          final String requestBody, final Cookies cookies) {
        this.requestLine = requestLine;
        this.httpRequestHeaders = httpHeaders;
        this.requestBody = requestBody;
        this.cookies = cookies;
    }

    public static Http11Request from(final BufferedReader bufferedReader) throws IOException {
        final var requestLine = generateRequestLine(bufferedReader);
        final var httpHeaders = generateHttpHeader(bufferedReader);
        final var requestBody = generateHttpRequestBody(httpHeaders, bufferedReader);
        final var cookies = httpHeaders.getCookies();
        return new Http11Request(requestLine, httpHeaders, requestBody, cookies);
    }

    private static RequestLine generateRequestLine(final BufferedReader bufferedReader) throws IOException {
        final String line = bufferedReader.readLine();
        return RequestLine.from(line);
    }

    private static HttpRequestHeaders generateHttpHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> httpHeaders = new ArrayList<>();
        var line = bufferedReader.readLine();

        if (Objects.isNull(line)) {
            return HttpRequestHeaders.from(httpHeaders);
        }

        while (!"".equals(line)) {
            httpHeaders.add(line);
            line = bufferedReader.readLine();
        }
        return HttpRequestHeaders.from(httpHeaders);
    }

    public static String generateHttpRequestBody(final HttpRequestHeaders httpHeaders,
                                                 final BufferedReader bufferedReader)
            throws IOException {
        final var contentLength = httpHeaders.getContentLength();
        final var length = contentLength.getContentLength();
        final char[] body = new char[length];

        bufferedReader.read(body);

        return new String(body);
    }

    public boolean isStaticResource() {
        return requestLine.isStaticResource();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpRequestHeaders getHttpRequestHeaders() {
        return httpRequestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public Cookies getCookieByJSessionId() {
        return httpRequestHeaders.getCookies();
    }

    public Session getSession() {
        final Optional<String> jSessionId = cookies.getJSessionId();
        return jSessionId.map(Session::new).orElseGet(() -> new Session(UUID.randomUUID().toString()));
    }

    public boolean hasCookieByJSessionId() {
        return cookies.hasCookieByJSessionId();
    }

    public Optional<String> getJSessionId() {
        return cookies.getJSessionId();
    }
}

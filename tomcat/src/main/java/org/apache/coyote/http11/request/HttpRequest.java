package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ParseUtils;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_LENGTH = "Content-Length";

    private final RequestLine startLine;
    private final RequestHeaders headers;
    private final HttpCookie httpCookie;
    private final String body;
    private final Session session;

    public HttpRequest(final RequestLine startLine, final RequestHeaders headers, final HttpCookie httpCookie,
                       final String body, final Session session) {
        this.startLine = startLine;
        this.headers = headers;
        this.httpCookie = httpCookie;
        this.body = body;
        this.session = session;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var startLine = reader.readLine();
        if (startLine.isEmpty()) {
            return null;
        }
        final var headers = readHeaders(reader);
        final var body = readBody(reader, headers.getContentLength());

        return HttpRequest.of(startLine, headers, body);
    }

    private static RequestHeaders readHeaders(final BufferedReader bufferedReader) throws IOException {
        StringBuilder headers = new StringBuilder();
        String line;
        while (!(line = Objects.requireNonNull(bufferedReader.readLine())).isBlank()) {
            headers.append(line)
                    .append("\r\n");
        }

        return RequestHeaders.from(headers.toString());
    }

    private static String readBody(final BufferedReader reader, final String contentLength) throws IOException {
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        reader.read(buffer, 0, length);

        return new String(buffer);
    }

    public static HttpRequest of(final String startLine, final RequestHeaders headers,
                                 final String requestBody) {
        HttpCookie httpCookies = HttpCookie.empty();

        if (headers.hasCookies()) {
            httpCookies = HttpCookie.from(headers.getCookies());
        }

        RequestLine requestLine = RequestLine.from(startLine);

        return new HttpRequest(requestLine, headers, httpCookies, requestBody, new Session());
    }

    public Map<String, String> getParsedRequestBody() {
        return ParseUtils.parse(body, "&", "=");
    }

    public String getPath() {
        return startLine.getPath();
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public String getBody() {
        return body;
    }

    public Session getSession() {
        return session;
    }

    public boolean isGet() {
        return startLine.isMethodGet();
    }

    public boolean isPost() {
        return startLine.isMethodPost();
    }
}

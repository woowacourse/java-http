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

    private final RequestLine requestline;
    private final RequestHeaders headers;
    private final HttpCookie httpCookie;
    private final String requestBody;
    private final Session session;

    public HttpRequest(final RequestLine requestline, final RequestHeaders headers, final HttpCookie httpCookie,
                       final String requestBody, final Session session) {
        this.requestline = requestline;
        this.headers = headers;
        this.httpCookie = httpCookie;
        this.requestBody = requestBody;
        this.session = session;
    }

    public static HttpRequest of(final BufferedReader reader) throws IOException {
        final var startLine = reader.readLine();
        if (startLine.isEmpty()) {
            return null;
        }
        final var requestHeaders = readRequestHeaders(reader);
        var requestBody = "";
        if (requestHeaders.hasRequestBody()) {
            requestBody = readRequestBody(reader, requestHeaders.getContentLength());
        }
        return HttpRequest.of(startLine, requestHeaders, requestBody);
    }

    private static RequestHeaders readRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        StringBuilder requestHeaders = new StringBuilder();
        String line;
        while (!(line = Objects.requireNonNull(bufferedReader.readLine())).isBlank()) {
            requestHeaders.append(line)
                    .append("\r\n");
        }
        return RequestHeaders.from(requestHeaders.toString());
    }

    private static String readRequestBody(final BufferedReader reader, final String contentLength) throws IOException {
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

    public Map<String, String> getParseRequestBody() {
        return ParseUtils.parse(requestBody, "&", "=");
    }


    public String getUrl() {
        return requestline.getPath();
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public Session getSession() {
        return session;
    }

    public boolean isGet() {
        return requestline.isMethodGet();
    }

    public boolean isPost() {
        return requestline.isMethodPost();
    }
}

package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ParseUtils;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_LENGTH = "Content-Length";

    private final RequestLine startLine;
    private final RequestHeaders headers;
    private final String body;

    public HttpRequest(final RequestLine startLine, final RequestHeaders headers, final String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var line = readStartLine(reader);
        final var headers = readHeaders(reader);
        final var body = readBody(reader, headers.getContentLength());

        return new HttpRequest(line, headers, body);
    }

    private static RequestLine readStartLine(final BufferedReader reader) throws IOException {
        final var startLine = reader.readLine();

        return RequestLine.from(startLine);
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

    public Map<String, String> getParsedRequestBody() {
        return ParseUtils.parse(body, "&", "=");
    }

    public String getPath() {
        return startLine.getPath();
    }

    public HttpCookie getCookie() {
        HttpCookie httpCookies = HttpCookie.empty();

        if (headers.hasCookies()) {
            httpCookies = HttpCookie.from(headers.getCookies());
        }
        return httpCookies;
    }


    public Session getSession() throws IOException {
        HttpCookie cookie = getCookie();
        String sessionId = cookie.getJSessionId();
        SessionManager sessionManager = new SessionManager();
        if (sessionId == null) {
            return null;
        }
        return sessionManager.findSession(sessionId);
    }

    public String getBody() {
        return body;
    }

    public boolean isGet() {
        return startLine.isMethodGet();
    }

    public boolean isPost() {
        return startLine.isMethodPost();
    }
}

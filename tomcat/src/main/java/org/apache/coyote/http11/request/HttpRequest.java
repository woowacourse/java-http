package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    private static final int REQUEST_START_INDEX = 0;

    private final HttpRequestLine line;
    private final HttpRequestHeaders headers;
    private final String requestBody;

    public HttpRequest(final HttpRequestLine line, final HttpRequestHeaders headers,
                       final String requestBody) {
        this.line = line;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(final BufferedReader reader) throws IOException {
        final List<String> lines = readAllLines(reader);
        int emptyLineIndex = getEmptyLineIndex(lines);
        final HttpRequestLine httpRequestLine = HttpRequestLine.parse(lines.get(REQUEST_START_INDEX));
        final HttpRequestHeaders httpRequestHeaders =
                HttpRequestHeaders.parse(lines.subList(REQUEST_START_INDEX + 1, emptyLineIndex));
        final String requestBody = findRequestBody(reader, httpRequestHeaders);
        return new HttpRequest(
                httpRequestLine,
                httpRequestHeaders,
                requestBody
        );
    }

    private static List<String> readAllLines(final BufferedReader reader) {
        final List<String> lines = new ArrayList<>();

        String line;
        while (!(line = readOneLine(reader)).equals("")) {
            lines.add(line);
        }
        return lines;
    }

    private static String readOneLine(final BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private static int getEmptyLineIndex(final List<String> lines) {
        final int emptyLineIndex = lines.indexOf("");
        if (emptyLineIndex == -1) {
            return lines.size();
        }
        return emptyLineIndex;
    }

    private static String findRequestBody(final BufferedReader reader,
                                          final HttpRequestHeaders httpRequestHeaders) throws IOException {
        if (httpRequestHeaders.hasRequestBody()) {
            final int contentLength = httpRequestHeaders.getContentLength();
            final char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    public Optional<User> findUserByJSessionId() {
        try {
            final String jSessionId = headers.getJSessionId();
            log.info("Request JSessionId={}", jSessionId);
            final Session session = SessionManager.findSession(jSessionId);
            return Optional.ofNullable((User) session.getAttribute("user"));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String getMethod() {
        return line.getMethod();
    }

    public String getRequestUrl() {
        return line.getRequestUrl();
    }

    public String getRequestBody() {
        return requestBody;
    }
}

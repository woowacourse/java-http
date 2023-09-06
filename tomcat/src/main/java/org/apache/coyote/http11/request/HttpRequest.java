package org.apache.coyote.http11.request;

import nextstep.jwp.model.User;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    public static final int REQUEST_START_INDEX = 0;

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final String requestBody;

    private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, String requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        List<String> lines = reaAllLines(reader);
        int emptyLineIndex = getEmptyLineIndex(lines);
        RequestLine requestLine = RequestLine.from(lines.get(REQUEST_START_INDEX));
        RequestHeaders headers = RequestHeaders.from(lines.subList(REQUEST_START_INDEX + 1, emptyLineIndex));
        String body = findRequestBody(reader, headers);
        return new HttpRequest(requestLine, headers, body);
    }

    private static List<String> reaAllLines(BufferedReader reader) {
        List<String> lines = new ArrayList<>();

        String line;
        while (!(line = readOneLine(reader)).equals("")) {
            lines.add(line);
        }
        return lines;
    }

    private static String readOneLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private static int getEmptyLineIndex(List<String> lines) {
        int emptyLineIndex = lines.indexOf("");
        if (emptyLineIndex == -1) {
            return lines.size();
        }
        return emptyLineIndex;
    }

    private static String findRequestBody(BufferedReader reader, RequestHeaders headers) throws IOException {
        if (headers.hasRequestBody()) {
            int contentLength = headers.getContentLength();
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    public Optional<User> findUserByJSessionId() {
        try {
            final String jSessionId = requestHeaders.getJSessionId();
            log.info("Request JSessionId={}", jSessionId);
            final Session session = SessionManager.findSession(jSessionId);
            return Optional.ofNullable((User) session.getAttribute("user"));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getRequestUrl() {
        return requestLine.getRequestUrl();
    }

    public String getRequestBody() {
        return requestBody;
    }

}

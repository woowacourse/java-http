package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Request {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String HEADER_KEY_VALUE_DELIMITER = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final int HEADER_INDEX_SIZE = 2;
    private static final SessionManager sessionManager = new SessionManager();
    private static final Logger log = LoggerFactory.getLogger(Http11Request.class);

    private final Http11RequestStartLine startLine;
    private final Http11RequestHeader headers;
    private final String body;

    public Http11Request(Http11RequestStartLine startLine, Http11RequestHeader headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static Http11Request from(InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        Http11RequestStartLine startLine = Http11RequestStartLine.from(bufferedReader.readLine());
        Http11RequestHeader httpHeaders = Http11RequestHeader.from(createHttpHeaderMap(bufferedReader));
        if (startLine.getMethod().hasBody()) {
            return new Http11Request(startLine, httpHeaders, createBody(bufferedReader, httpHeaders.getContentLength()));
        }
        return new Http11Request(startLine, httpHeaders, null);
    }

    private static Map<String, List<String>> createHttpHeaderMap(BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .map(line -> line.split(HEADER_KEY_VALUE_DELIMITER))
                .filter(parts -> parts.length == HEADER_INDEX_SIZE)
                .collect(groupingBy(parts -> parts[HEADER_KEY_INDEX].trim(),
                        mapping(parts -> parts[HEADER_VALUE_INDEX].trim(), toList())));
    }

    private static String createBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    public boolean hasSession() {
        Optional<String> optionalSessionId = headers.getCookieValue(JSESSIONID);

        if (optionalSessionId.isPresent()) {
            String sessionId = optionalSessionId.get();
            try {
                sessionManager.findSession(sessionId);
                return true;
            } catch (IllegalArgumentException e) {
                log.info("Invalid Session ID = {}", sessionId);
                return false;
            }
        }
        return false;
    }

    public boolean isStaticResourceRequest() {
        return startLine.getMethod() == HttpMethod.GET && startLine.getEndPoint().contains(".");
    }

    public Session getSession(boolean hasNotSession) {
        if (hasNotSession) {
            Session session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
            return session;
        }
        String sessionId = headers.getCookieValue(JSESSIONID)
                .orElseThrow(() -> new IllegalArgumentException("세션 ID가 존재하지 않습니다."));
        return sessionManager.findSession(sessionId);
    }

    public String getAccept() {
        return headers.getAccept();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getEndpoint() {
        return startLine.getEndPoint();
    }

    public Http11RequestHeader getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Http11Request{" +
               "startLine=" + startLine +
               ", headers=" + headers +
               ", body='" + body + '\'' +
               '}';
    }
}

package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpHeaders;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import org.apache.coyote.cookie.Cookies;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Request {

    private static final BiPredicate<String, String> HEADER_FILTER = (a, b) -> true;
    private static final String HEADER_KEY_VALUE_DELIMITER = ": ";
    private static final String HEADER_DETAIL_DELIMITER = ";";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final Logger log = LoggerFactory.getLogger(Http11Request.class);

    private final Http11RequestStartLine startLine;
    private final HttpHeaders headers;
    private final String body;
    private final SessionManager sessionManager;

    public Http11Request(Http11RequestStartLine startLine, HttpHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
        this.sessionManager = new SessionManager();
    }

    public static Http11Request from(InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        Http11RequestStartLine startLine = Http11RequestStartLine.from(bufferedReader.readLine());
        HttpHeaders httpHeaders = HttpHeaders.of(createHttpHeaderMap(bufferedReader), HEADER_FILTER);
        if (startLine.getMethod().hasBody()) {
            return new Http11Request(startLine, httpHeaders, createBody(bufferedReader, getContentLength(httpHeaders)));
        }
        return new Http11Request(startLine, httpHeaders, null);
    }

    private static Map<String, List<String>> createHttpHeaderMap(BufferedReader bufferedReader) throws IOException {
        Map<String, List<String>> httpHeaders = new HashMap<>();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.isBlank()) {
                break;
            }
            String[] header = line.split(HEADER_KEY_VALUE_DELIMITER);
            String key = header[HEADER_KEY_INDEX];
            String value = header[HEADER_VALUE_INDEX];

            List<String> values = Arrays.stream(value.split(HEADER_DETAIL_DELIMITER)).map(String::trim).toList();
            httpHeaders.put(key, values);
        }
        return httpHeaders;
    }

    private static int getContentLength(HttpHeaders httpHeaders) {
        return httpHeaders.firstValue("Content-Length").map(Integer::parseInt).orElse(0);
    }

    private static String createBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    public boolean hasSession() {
        Cookies cookies = Cookies.from(headers.allValues("Cookie"));
        Optional<String> optionalSessionId = cookies.getValue("JSESSIONID");
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
        Cookies cookies = Cookies.from(headers.allValues("Cookie"));
        String sessionId = cookies.getValue("JSESSIONID")
                .orElseThrow(() -> new IllegalArgumentException("세션 ID가 존재하지 않습니다."));

        return sessionManager.findSession(sessionId);
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getEndpoint() {
        return startLine.getEndPoint();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Optional<String> getHeaderFirstValue(String key) {
        return headers.firstValue(key);
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

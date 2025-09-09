package org.apache.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.exception.InvalidRequestException;
import org.apache.exception.SocketReadException;
import org.apache.http.value.HttpHeader;
import org.apache.http.value.HttpMethod;
import org.apache.http.value.HttpVersion;

public class HttpRequest {

    private final HttpMethod method;
    private final String uri;
    private final Map<String, String> queryStrings;
    private final HttpVersion version;
    private final Map<String, String> headers;
    private final Map<String, Cookie> cookies;
    private final Map<String, String> body;
    //TODO: 요청 메세지 파트별 클래스 분리 고려  (2025-09-9, 화, 13:43)

    public HttpRequest(InputStream inputStream) {
        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            List<String> startLinePart = readStartLine(bufferedReader);
            this.method = HttpMethod.valueOf(startLinePart.get(0));
            this.uri = parseUri(startLinePart.get(1));
            this.version = HttpVersion.parse(startLinePart.get(2));
            this.queryStrings = parseQueryString(startLinePart.get(1));
            this.headers = readHeader(bufferedReader);
            this.cookies = parseCookie();
            this.body = readBody(bufferedReader);
        } catch (IOException e) {
            throw new SocketReadException("HTTP 요청 메세지가 올바르지 않습니다.");
        }
    }

    public boolean checkQueryStringExistence(String key) {
        return queryStrings.containsKey(key);
    }

    public boolean checkHeaderExistence(String key) {
        return headers.containsKey(key);
    }

    public boolean checkCookieExistence(String key) {
        return cookies.containsKey(key);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public String getQueryString(String key) {
        return queryStrings.get(key);
    }

    public String getHeader(String key) {
        return headers.get(key.toLowerCase());
    }

    public Cookie getCookie(String key) {
        return cookies.get(key);
    }

    public String getBody(String key) {
        return body.get(key);
    }

    private List<String> readStartLine(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        validateEmptyStartLine(startLine);
        validateStartLineFormat(startLine);
        return List.of(startLine.split("\\s+"));
    }

    private Map<String, String> readHeader(BufferedReader reader) throws IOException {
        Map<String, String> headerRead = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            int separatorIndex = line.indexOf(":");
            String key = line.substring(0, separatorIndex).trim().toLowerCase();
            String value = line.substring(separatorIndex + 1).trim();
            headerRead.put(key, value);
        }
        return headerRead;
    }

    private Map<String, String> readBody(BufferedReader reader) throws IOException {
        Map<String, String> bodyRead = new HashMap<>();

        if (!checkHeaderExistence(HttpHeader.CONTENT_LENGTH.getValue())) {
            return bodyRead;
        }

        int contentLength = Integer.parseInt(getHeader(HttpHeader.CONTENT_LENGTH.getValue()));
        char[] buffer = new char[contentLength];
        int read = reader.read(buffer, 0, contentLength);
        String bodyText = new String(buffer, 0, read);
        String decoded = URLDecoder.decode(bodyText, StandardCharsets.UTF_8);

        List<String> bodyParts = List.of(decoded.split("&"));
        for (String bodyPart : bodyParts) {
            List<String> keyValue = List.of(bodyPart.split("="));
            String key = keyValue.getFirst().trim();
            String value = keyValue.getLast().trim();
            bodyRead.put(key, value);
        }
        return bodyRead;
    }

    private String parseUri(String uriLine) {
        List<String> startLinePart = List.of(uriLine.split("\\?"));
        return startLinePart.getFirst();
    }

    private Map<String, String> parseQueryString(String uriLine) {
        Map<String, String> queryStringRead = new HashMap<>();

        if (!hasQueryParam(uriLine)) {
            return queryStringRead;
        }

        List<String> startLinePart = List.of(uriLine.split("\\?"));
        String queryStringLine = startLinePart.getLast();
        List<String> queryStringParts = List.of(queryStringLine.split("&"));
        for (String queryStringPart : queryStringParts) {
            List<String> keyValue = List.of(queryStringPart.split("="));
            queryStringRead.put(keyValue.getFirst(), keyValue.getLast());
        }
        return queryStringRead;
    }

    private Map<String, Cookie> parseCookie() {
        Map<String, Cookie> cookieRead = new HashMap<>();

        boolean cookieHeaderExistence = checkHeaderExistence(HttpHeader.COOKIE.getValue());
        if (!cookieHeaderExistence) {
            return cookieRead;
        }

        String cookieHeader = getHeader(HttpHeader.COOKIE.getValue());
        List<String> cookieLines = List.of(cookieHeader.split(";"));
        for (String cookieLine : cookieLines) {
            int index = cookieLine.indexOf("=");
            String key = cookieLine.substring(0, index).trim();
            String value = cookieLine.substring(index + 1).trim();
            cookieRead.put(key, new Cookie(key, value));
        }
        return cookieRead;
    }

    private boolean hasQueryParam(String uriLine) {
        return uriLine.contains("?")
                && uriLine.indexOf("?") != uriLine.length() - 1;
    }

    private void validateEmptyStartLine(String startLine) {
        if (startLine == null || startLine.isEmpty()) {
            throw new InvalidRequestException("요청 메세지의 시작라인 형식이 올바르지 않습니다.");
        }
    }

    private void validateStartLineFormat(String startLine) {
        List<String> startLinePart = List.of(startLine.split(" "));
        if (startLinePart.size() < 3) {
            throw new InvalidRequestException("요청 메세지의 시작라인 형식이 올바르지 않습니다.");
        }
    }
}

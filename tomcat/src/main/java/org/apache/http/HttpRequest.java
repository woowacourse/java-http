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

public class HttpRequest {

    private final HttpMethod method;
    private final String uri;
    private final Map<String, String> queryString = new HashMap<>();
    private final HttpVersion version;
    private final Map<String, String> header = new HashMap<>();
    private final Map<String, String> body = new HashMap<>();

    public HttpRequest(InputStream inputStream) {
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        try {
            List<String> startLinePart = readStartLine(bufferedReader);
            this.method = HttpMethod.valueOf(startLinePart.get(0));
            this.uri = parseUri(startLinePart.get(1));
            this.version = HttpVersion.parse(startLinePart.get(2));
            parseQueryParam(startLinePart.get(1));

            readHeader(bufferedReader);
            if (checkHeaderExistence("Content-Length")) {
                readBody(bufferedReader);
            }
        } catch (IOException e) {
            throw new SocketReadException("HTTP 요청 메세지가 올바르지 않습니다.");
        }
    }

    public boolean checkQueryStringExistence(String key) {
        return queryString.containsKey(key);
    }

    public boolean checkHeaderExistence(String key) {
        return header.containsKey(key);
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
        return queryString.get(key);
    }

    public String getHeader(String key) {
        return header.get(key);
    }

    public String getBody(String key) {
        return body.get(key);
    }

    private List<String> readStartLine(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        validateStartLineFormat(startLine);
        return List.of(startLine.split(" "));
    }

    private void readHeader(BufferedReader reader) throws IOException {
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            List<String> headerLinePart = List.of(line.split(":"));
            String key = headerLinePart.getFirst().trim();
            String value = headerLinePart.getLast().trim();
            header.put(key, value);
        }
    }

    private void readBody(BufferedReader reader) throws IOException {
        int contentLength = Integer.parseInt(getHeader("Content-Length"));
        char[] buffer = new char[contentLength];
        int read = reader.read(buffer, 0, contentLength);
        String bodyText = new String(buffer, 0, read);
        String decoded = URLDecoder.decode(bodyText, StandardCharsets.UTF_8);

        List<String> bodyParts = List.of(decoded.split("&"));
        for (String bodyPart : bodyParts) {
            List<String> keyValue = List.of(bodyPart.split("="));
            String key = keyValue.getFirst().trim();
            String value = keyValue.getLast().trim();
            body.put(key, value);
        }
    }

    private String parseUri(String uriLine) {
        List<String> startLinePart = List.of(uriLine.split("\\?"));
        return startLinePart.getFirst();
    }

    private void parseQueryParam(String uriLine) {
        if (!hasQueryParam(uriLine)) {
            return;
        }

        List<String> startLinePart = List.of(uriLine.split("\\?"));
        String queryStringLine = startLinePart.getLast();
        List<String> queryStringParts = List.of(queryStringLine.split("&"));
        for (String queryStringPart : queryStringParts) {
            List<String> keyValue = List.of(queryStringPart.split("="));
            queryString.put(keyValue.getFirst(), keyValue.getLast());
        }
    }

    private void validateStartLineFormat(String startLine) {
        List<String> startLinePart = List.of(startLine.split(" "));
        if (startLinePart.size() < 3) {
            throw new InvalidRequestException("요청 메세지의 시작라인 형식이 올바르지 않습니다.");
        }
    }

    private boolean hasQueryParam(String uriLine) {
        return uriLine.contains("?")
                && uriLine.indexOf("?") != uriLine.length() - 1;
    }
}

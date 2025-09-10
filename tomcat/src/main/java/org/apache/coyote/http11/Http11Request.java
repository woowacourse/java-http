package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Http11Request {

    private final StartLine startLine;
    private final List<Header> headers;
    private final String body;

    private Http11Request(final List<String> lines, final String body) {
        this.startLine = extractStartLine(lines.getFirst());
        this.headers = extractHeaders(lines);
        this.body = body;
    }

    public static Http11Request from(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> startLineWithHeaders = extractRequestHeadersWithStartLine(bufferedReader);
        final String body = extractRequestBody(bufferedReader, startLineWithHeaders);

        return new Http11Request(startLineWithHeaders, body);
    }

    private static List<String> extractRequestHeadersWithStartLine(final BufferedReader bufferedReader)
            throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        String requestLine;
        while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isEmpty()) {
            requestHeaders.add(requestLine);
        }

        return requestHeaders;
    }

    private static String extractRequestBody(final BufferedReader bufferedReader, final List<String> headers)
            throws IOException {
        //Content-Length 헤더가 있는지 봐야함.
        final int contentLength = headers.stream()
                .filter(header -> header.startsWith("Content-Length:"))
                .map(header -> header.split(":", 2)[1].trim())
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(0);

        if (contentLength == 0) {
            return "";
        }

        char[] bodyChars = new char[contentLength];
        int readCount = bufferedReader.read(bodyChars);

        if (readCount == -1) {
            return "";
        }

        return new String(bodyChars);
    }

    public Map<String, String> extractRequestBodyParams() {
        return Arrays.stream(body.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
    }

    public Session getSession(final boolean create) {
        for (Header header : headers) {
            if (header.isCookeHeader()) {
                final Http11Cookie cookie = new Http11Cookie(header.getValue());
                final String sessionId = cookie.get("JSESSIONID");
                final Session session = SessionManager.findSession(sessionId);
                if (session != null) {
                    return session;
                }
                break;
            }
        }

        if (create) {
            final String newSessionId = UUID.randomUUID().toString();
            final Session newSession = new Session(newSessionId);
            SessionManager.add(newSession);

            return newSession;
        }

        return null;
    }

    public String getUri() {
        return startLine.getUri();
    }

    public HttpMethod getHttpMethod() {
        return startLine.getHttpMethod();
    }

    private List<Header> extractHeaders(final List<String> lines) {
        List<Header> headerList = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] splittedLines = line.split(":", 2);
            if (splittedLines.length == 2) {
                headerList.add(new Header(splittedLines[0].trim(), splittedLines[1].trim()));
            }
        }

        return headerList;
    }

    private StartLine extractStartLine(final String startLine) {
        final String[] startLineValues = startLine.split(" ");

        validateStartLineCounts(startLineValues);

        final HttpMethod httpMethod = HttpMethod.parseHttpMethodFrom(startLineValues[0]);
        final String uri = startLineValues[1];
        final HttpVersion httpVersion = HttpVersion.parseHttpVersionFrom(startLineValues[2]);

        return new StartLine(httpMethod, uri, httpVersion);
    }

    private void validateStartLineCounts(final String[] startLineValues) {
        if (startLineValues.length != 3) {
            throw new IllegalArgumentException("StartLine의 3개의 값이 아닙니다.");
        }
    }
}

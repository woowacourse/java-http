package org.apache.coyote.http11.http.request;

import java.util.List;
import org.apache.coyote.http11.http.HttpVersion;

public class HttpRequestStartLine {

    private static final int START_LINE_MIN_LENGTH = 3;

    private final HttpMethod httpMethod;
    private final String path;
    private final HttpVersion httpVersion;

    private HttpRequestStartLine(final HttpMethod httpMethod, final String path, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestStartLine of(final String startLine) {
        return parseStartLine(startLine);
    }

    private static HttpRequestStartLine parseStartLine(final String startLine) {
        final List<String> startLineInfos = parseStartLineInfos(startLine);
        final HttpMethod method = HttpMethod.of(startLineInfos.get(0));
        final String path = startLineInfos.get(1);
        final HttpVersion version = HttpVersion.of(startLineInfos.get(2));

        return new HttpRequestStartLine(method, path, version);
    }

    private static List<String> parseStartLineInfos(final String startLine) {
        final List<String> startLineInfos = List.of(startLine.split(" "));
        validateStartLineLength(startLineInfos);
        return startLineInfos;
    }

    private static void validateStartLineLength(final List<String> startLineInfos) {
        if (startLineInfos.size() < START_LINE_MIN_LENGTH) {
            throw new IllegalArgumentException("요청 정보가 잘못되었습니다.");
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}

package org.apache.coyote.http11.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.http.common.HttpSplitFormat;
import org.apache.coyote.http11.http.common.startline.HttpMethod;
import org.apache.coyote.http11.http.common.startline.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpStartLine {

    private static final Logger log = LoggerFactory.getLogger(HttpStartLine.class);

    private final HttpMethod method;
    private final HttpRequestPath path;
    private final HttpVersion version;

    private HttpStartLine(final HttpMethod method, final HttpRequestPath path, final HttpVersion version) {
        this.method = method;
        this.path = path;
        this.version = version;

    }

    public static HttpStartLine from(final BufferedReader bufferedReader) throws IOException {
        validateNull(bufferedReader);
        final String httpRequestLine = bufferedReader.readLine();
        validateNull(httpRequestLine);
        final String[] httpRequestElements = httpRequestLine.split(HttpSplitFormat.START_LINE.getValue());
        validateFormat(httpRequestElements);
        final HttpMethod method = HttpMethod.findMethod(httpRequestElements[0].trim());
        final HttpRequestPath path = HttpRequestPath.from(httpRequestElements[1].trim());
        final HttpVersion version = HttpVersion.find(httpRequestElements[2].trim());
        return new HttpStartLine(method, path, version);
    }

    private static void validateNull(final BufferedReader bufferedReader) {
        if (bufferedReader == null) {
            throw new IllegalArgumentException("bufferedReader는 null일 수 없습니다.");
        }
    }

    private static void validateNull(final String httpRequestLine) {
        if (httpRequestLine == null) {
            throw new IllegalArgumentException("유효하지 않은 Http request line 형식입니다");
        }
    }

    private static void validateFormat(final String[] httpRequestElements) {
        if (httpRequestElements == null) {
            throw new IllegalArgumentException("httpRequestElements null일 수 없습니다");
        }
        if (httpRequestElements.length != 3) {
            log.warn("invalid http request elements length {}", httpRequestElements.length);
            throw new IllegalArgumentException(
                    "httpRequestElements는 3개로 구성되어야 합니다: %d".formatted(httpRequestElements.length));
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path.getRootPath();
    }

    public String getTargetQueryParameter(final String target) {
        return path.getTargetQueryParameter(target);
    }

    public String getHttpVersion() {
        return version.getValue();
    }
}

package org.apache.coyote.http11.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.http.common.HttpSplitFormat;
import org.apache.coyote.http11.http.common.startline.HttpMethod;
import org.apache.coyote.http11.http.common.startline.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestLine {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLine.class);

    private final HttpMethod method;
    private final HttpRequestPath path;
    private final HttpVersion version;

    public static HttpRequestLine from(final BufferedReader bufferedReader) throws IOException {
        validateNull(bufferedReader);
        return new HttpRequestLine(bufferedReader);
    }

    private HttpRequestLine(final BufferedReader bufferedReader) throws IOException {
        String httpRequestLine = bufferedReader.readLine();
        String[] httpRequestElements = httpRequestLine.split(HttpSplitFormat.START_LINE.getValue());
        validateFormat(httpRequestElements);
        this.method = HttpMethod.findMethod(clean(httpRequestElements[0]));
        this.path = HttpRequestPath.from(clean(httpRequestElements[1]));
        this.version = HttpVersion.find(clean(httpRequestElements[2]));
    }

    private void validateFormat(final String[] httpRequestElements) {
        if (httpRequestElements == null) {
            throw new IllegalArgumentException("httpRequestElements null일 수 없습니다");
        }
        if (httpRequestElements.length != 3) {
            log.warn("invalid http request elements length {}", httpRequestElements.length);
            throw new IllegalArgumentException(
                    "httpRequestElements는 3개로 구성되어야 합니다: %d".formatted(httpRequestElements.length));
        }
    }

    private static void validateNull(final BufferedReader bufferedReader) {
        if (bufferedReader == null) {
            throw new IllegalArgumentException("bufferedReader는 null일 수 없습니다.");
        }
    }

    private String clean(final String target) {
        return target.trim();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path.getPath();
    }

    public String getTargetQueryParameter(final String target) {
        return path.getTargetQueryParameter(target);
    }

    public String getHttpVersion() {
        return version.getValue();
    }
}

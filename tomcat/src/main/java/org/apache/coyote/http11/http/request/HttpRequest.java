package org.apache.coyote.http11.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.http.common.HttpCookie;
import org.apache.coyote.http11.http.common.header.HttpHeader;
import org.apache.coyote.http11.http.common.startline.HttpMethod;

public class HttpRequest {

    private final HttpStartLine httpStartLine;
    private final HttpHeader httpHeader;
    private final HttpCookie httpCookie;
    private final HttpRequestBody httpRequestBody;

    private HttpRequest(final HttpStartLine httpStartLine,
                        final HttpHeader httpHeader,
                        final HttpCookie httpCookie,
                        final HttpRequestBody httpRequestBody) {
        this.httpStartLine = httpStartLine;
        this.httpHeader = httpHeader;
        this.httpCookie = httpCookie;
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        validateNull(inputStream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final HttpStartLine httpStartLine = HttpStartLine.from(bufferedReader);
        final HttpHeader httpHeader = HttpHeader.from(bufferedReader);
        final HttpCookie httpCookie = HttpCookie.from(httpHeader);
        final HttpRequestBody httpRequestBody = HttpRequestBody.of(bufferedReader, httpHeader);
        return new HttpRequest(httpStartLine, httpHeader, httpCookie, httpRequestBody);
    }

    private static void validateNull(final InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream은 null일 수 없습니다.");
        }
    }

    public HttpMethod getMethod() {
        return httpStartLine.getMethod();
    }

    public String getPath() {
        return httpStartLine.getPath();
    }

    public boolean containsQueryParameter(final String target) {
        if (target == null) {
            throw new IllegalArgumentException("찾으려는 query parameter key는 null일 수 없습니다.");
        }
        String cleanTarget = target.trim();

        return httpStartLine.containsTargetQueryParameter(cleanTarget);
    }

    public String getTargetQueryParameter(final String target) {
        return httpStartLine.getTargetQueryParameter(target);
    }

    public HttpRequestBody getBody() {
        return httpRequestBody;
    }

    public HttpCookie getCookie() {
        return httpCookie;
    }
}

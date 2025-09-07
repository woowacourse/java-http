package org.apache.coyote.http11.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.http.common.header.HttpHeader;
import org.apache.coyote.http11.http.common.startline.HttpMethod;

public class HttpRequest {

    private final HttpStartLine httpStartLine;
    private final HttpHeader httpHeader;
    private final HttpRequestBody httpRequestBody;

    private HttpRequest(final HttpStartLine httpStartLine,
                        final HttpHeader httpHeader,
                        final HttpRequestBody httpRequestBody) {
        this.httpStartLine = httpStartLine;
        this.httpHeader = httpHeader;
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        validateNull(inputStream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpStartLine httpStartLine = HttpStartLine.from(bufferedReader);
        final HttpHeader httpHeader = HttpHeader.from(bufferedReader);
        final HttpRequestBody httpRequestBody = HttpRequestBody.of(inputStream, httpHeader);
        return new HttpRequest(httpStartLine, httpHeader, httpRequestBody);
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

    public String getTargetQueryParameter(String target) {
        return httpStartLine.getTargetQueryParameter(target);
    }
}

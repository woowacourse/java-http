package org.apache.coyote.http11.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.http.common.header.HttpHeader;
import org.apache.coyote.http11.http.common.startline.HttpMethod;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpHeader httpHeader;
    private final HttpRequestBody httpRequestBody;

    private HttpRequest(final HttpRequestLine httpRequestLine,
                        final HttpHeader httpHeader,
                        final HttpRequestBody httpRequestBody) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeader = httpHeader;
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        validateNull(inputStream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpRequestLine httpRequestLine = HttpRequestLine.from(bufferedReader);
        final HttpHeader httpHeader = HttpHeader.from(bufferedReader);
        final HttpRequestBody httpRequestBody = HttpRequestBody.of(inputStream, httpHeader);
        return new HttpRequest(httpRequestLine, httpHeader, httpRequestBody);
    }

    private static void validateNull(final InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream은 null일 수 없습니다.");
        }
    }

    public HttpMethod getMethod() {
        return httpRequestLine.getMethod();
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public String getTargetQueryParameter(String target) {
        return httpRequestLine.getTargetQueryParameter(target);
    }
}

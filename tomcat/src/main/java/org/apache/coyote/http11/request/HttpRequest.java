package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.coyote.http11.HttpHeaders;

public class HttpRequest {

    private static final String EMPTY_BODY = "";
    private final HttpRequestStartLine httpRequestStartLine;
    private final HttpRequestHeaders httpRequestHeaders;
    private final String requestBody;

    private HttpRequest(
            final HttpRequestStartLine requestLine,
            final HttpRequestHeaders httpRequestHeaders,
            final String requestBody
    ) {
        this.httpRequestStartLine = requestLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(InputStream inputStream) throws IOException {
        Reader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        HttpRequestStartLine requestLine = HttpRequestStartLine.of(bufferedReader.readLine());
        HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.of(bufferedReader);

        String findContentLength = httpRequestHeaders.getValue(HttpHeaders.CONTENT_LENGTH);
        String requestBody = readRequestBody(findContentLength, bufferedReader);

        return new HttpRequest(requestLine, httpRequestHeaders, requestBody);
    }

    private static String readRequestBody(final String findContentLength, final BufferedReader bufferedReader)
            throws IOException {
        if (findContentLength != null) {
            int contentLength = Integer.parseInt(findContentLength);
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return EMPTY_BODY;
    }

    public String getPath() {
        return httpRequestStartLine.getUri().getPath();
    }

    public boolean isSameMethod(HttpMethod method) {
        return httpRequestStartLine.getHttpMethod() == method;
    }

    public HttpRequestStartLine getHttpRequestStartLine() {
        return httpRequestStartLine;
    }

    public HttpRequestHeaders getHttpRequestHeaders() {
        return httpRequestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }
}

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
    private final HttpHeaders httpHeaders;
    private final String requestBody;

    private HttpRequest(final HttpRequestStartLine requestLine, final HttpHeaders httpHeaders,
                        final String requestBody) {
        this.httpRequestStartLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(InputStream inputStream) throws IOException {
        Reader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        HttpRequestStartLine requestLine = HttpRequestStartLine.of(bufferedReader.readLine());
        HttpHeaders httpHeaders = HttpHeaders.of(bufferedReader);

        String findContentLength = httpHeaders.getValue("content-length");
        if (findContentLength != null) {
            int contentLength = Integer.parseInt(findContentLength);
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            return new HttpRequest(requestLine, httpHeaders, requestBody);
        }
        return new HttpRequest(requestLine, httpHeaders, EMPTY_BODY);
    }

    public String getHttpVersion() {
        return httpRequestStartLine.getHttpVersion();
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getPath() {
        return httpRequestStartLine.getUri().getPath();
    }

    public String getQuery() {
        return httpRequestStartLine.getUri().getQuery();
    }

    public boolean isSameMethod(String method) {
        return httpRequestStartLine.getHttpMethod()
                .equals(HttpMethod.valueOf(method));
    }
}

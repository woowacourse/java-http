package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.coyote.http11.HttpHeaders;

public class HttpRequest {

    private final HttpRequestStartLine httpRequestStartLine;
    private final HttpHeaders httpHeaders;

    private HttpRequest(final HttpRequestStartLine requestLine, final HttpHeaders httpHeaders) {
        this.httpRequestStartLine = requestLine;
        this.httpHeaders = httpHeaders;
    }

    public static HttpRequest of(InputStream inputStream) throws IOException {
        Reader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        HttpRequestStartLine requestLine = HttpRequestStartLine.of(bufferedReader.readLine());
        HttpHeaders httpHeaders = HttpHeaders.of(bufferedReader);

        return new HttpRequest(requestLine, httpHeaders);
    }

    public String getHttpVersion() {
        return httpRequestStartLine.getHttpVersion();
    }

    public String getPath() {
        return httpRequestStartLine.getUri().getPath();
    }

    public String getQuery(){
        return httpRequestStartLine.getUri().getQuery();
    }

    public boolean isSameMethod(String method) {
        return httpRequestStartLine.getHttpMethod()
                .equals(HttpMethod.valueOf(method));
    }
}

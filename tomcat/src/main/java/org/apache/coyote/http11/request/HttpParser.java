package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpParser {

    public static HttpRequest getRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequestHeader httpRequestHeader = parseHeader(reader);
        HttpRequestBody httpRequestBody = parseBody(reader, httpRequestHeader);
        HttpRequestParams httpRequestParams = parseParams(httpRequestHeader, httpRequestBody);
        return new HttpRequest(httpRequestHeader, httpRequestParams);
    }

    private static HttpRequestHeader parseHeader(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        HttpHeaders httpHeaders = HttpHeaders.of(reader);
        return HttpRequestHeader.from(requestLine, httpHeaders);
    }

    private static HttpRequestBody parseBody(BufferedReader reader, HttpRequestHeader httpRequestHeader)
            throws IOException {
        int contentLength = httpRequestHeader.getContentLength();
        if (contentLength <= 0) {
            return new HttpRequestBody("");
        }
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return new HttpRequestBody(requestBody);
    }

    private static HttpRequestParams parseParams(HttpRequestHeader httpRequestHeader, HttpRequestBody httpRequestBody) {
        String queryString = httpRequestHeader.getQueryString();
        if (httpRequestHeader.getMethod() == HttpMethod.POST) {
            queryString = httpRequestBody.rawBody();
        }
        return HttpRequestParams.of(queryString);
    }
}

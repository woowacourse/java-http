package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class HttpRequestFactory {

    private HttpRequestFactory() {
    }

    public static HttpRequest create(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String requestLine = reader.readLine();
        if (requestLine == null) {
            return null;
        }

        List<String> headers = reader.lines()
                .takeWhile(s -> !s.isBlank())
                .toList();

        HttpRequest httpRequest = new HttpRequest(requestLine, headers);
        setRequestBodyIfPresent(reader, httpRequest);
        return httpRequest;
    }

    private static void setRequestBodyIfPresent(BufferedReader reader, HttpRequest httpRequest) throws IOException {
        int contentLength = httpRequest.getContentLength();
        if (contentLength == 0) {
            return;
        }

        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        httpRequest.setRequestBody(requestBody);
    }
}

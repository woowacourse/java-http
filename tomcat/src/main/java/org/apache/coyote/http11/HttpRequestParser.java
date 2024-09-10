package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = bufferedReader.readLine();

        HttpHeaders headers = new HttpHeaders();
        bufferedReader.lines()
                .takeWhile(line -> !line.isEmpty())
                .forEach(headers::add);

        int contentLength = headers.getContentLength();
        char[] rawBody = new char[contentLength];
        bufferedReader.read(rawBody, 0, contentLength);
        String body = new String(rawBody);

        return new HttpRequest(requestLine, headers, body);
    }
}

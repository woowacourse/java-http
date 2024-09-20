package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http11.headers.HttpHeaders;

public class HttpRequestParser {

    public static HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();

        HttpHeaders headers = new HttpHeaders();
        bufferedReader.lines()
                .takeWhile(headerLine -> !headerLine.isEmpty())
                .forEach(headers::add);

        int contentLength = headers.getContentLength();
        char[] rawBody = new char[contentLength];
        bufferedReader.read(rawBody, 0, contentLength);
        RequestBody body = new RequestBody(rawBody);

        return new HttpRequest(requestLine, headers, body);
    }
}

package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = bufferedReader.readLine();
        HttpRequest request = new HttpRequest(requestLine);

        String headerLine;
        while (!(headerLine = bufferedReader.readLine()).isEmpty()) {
            request.addHeader(headerLine);
        }

        if (request.isPostMethod()) {
            int contentLength = request.getContentLength();
            char[] rawBody = new char[contentLength];
            bufferedReader.read(rawBody, 0, contentLength);
            request.addBody(new String(rawBody));
        }

        return request;
    }
}

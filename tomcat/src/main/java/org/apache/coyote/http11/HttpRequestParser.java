package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestParser {

    public static HttpRequest parse(BufferedReader reader) throws IOException {
        HttpRequestLine httpRequestLine = HttpRequestLine.parse(reader.readLine());
        HttpHeaders headers = HttpHeaders.parse(reader);
        HttpQueryParams httpQueryParams;
        String body = null;

        if ("POST".equalsIgnoreCase(httpRequestLine.getMethod())) {
            body = readBody(reader, headers.getContentLength());
            httpQueryParams = HttpQueryParams.fromBody(body);
        } else {
            httpQueryParams = HttpQueryParams.fromPath(httpRequestLine.getFullPath());
        }

        return new HttpRequest(httpRequestLine, headers, httpQueryParams, body);
    }

    private static String readBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength <= 0) {
            return null;
        }
        char[] bodyChars = new char[contentLength];
        reader.read(bodyChars);
        return new String(bodyChars);
    }
}

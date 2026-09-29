package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String body;

    private HttpRequest(
            final RequestLine requestLine,
            final HttpHeaders headers,
            final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        HttpHeaders headers = HttpHeaders.from(bufferedReader);
        String body = readBody(bufferedReader, headers);
        return new HttpRequest(requestLine, headers, body);
    }

    private static String readBody(BufferedReader bufferedReader, HttpHeaders headers) throws IOException {
        String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return "";
        }
        char[] body = new char[Integer.parseInt(contentLength)];
        int read = 0;
        while (read < body.length) {
            int count = bufferedReader.read(body, read, body.length - read);
            if (count < 0) {
                break;
            }
            read += count;
        }
        return new String(body, 0, read);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}

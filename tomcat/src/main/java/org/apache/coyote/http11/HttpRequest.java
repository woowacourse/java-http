package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;


    private HttpRequest(final RequestLine requestLine,
                        final Map<String, String> headers,
                        final  String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        final Map<String, String> headers = collectHeaders(bufferedReader);

        if (requestLine.method().equals(HttpMethod.POST)) {
            int contentLength = Integer.parseInt(headers.get(HttpHeader.CONTENT_LENGTH.getHeaderName()));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            return new HttpRequest(requestLine, headers, requestBody);
        }
        return new HttpRequest(requestLine, headers, null);
    }

    private static Map<String, String> collectHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String requestLine;
        while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isBlank()) {
            final String[] headerPair = requestLine.split(": ");
            headers.put(headerPair[0], headerPair[1]);
        }
        return headers;
    }



    public Cookie getCookie() {
        final String cookie = headers.get("Cookie");
        if (cookie != null) {
            return new Cookie(cookie);
        }
        return null;
    }

    public HttpMethod getMethod() {
        return requestLine.method();
    }

    public String getUri() {
        return requestLine.uri();
    }

    public String getVersion() {
        return requestLine.version();
    }

    public String getAcceptLine() {
        return headers.get("Accept");
    }

    public String getBody() {
        return body;
    }
}

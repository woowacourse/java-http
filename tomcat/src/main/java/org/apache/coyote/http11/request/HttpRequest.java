package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String EMPTY_LINE = "";
    private static final String HEADER_REGEX = ": ";
    private static final int HEADER_KEY = 0;
    private static final int HEADER_VALUE = 1;

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final Body body;

    private HttpRequest(RequestLine requestLine, RequestHeader header, Body body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        Map<String, String> header = extractHeader(bufferedReader);
        Body body = extractBody(bufferedReader, header);
        return new HttpRequest(requestLine, RequestHeader.from(header), body);
    }

    private static Map<String, String> extractHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestHeader = new HashMap<>();
        String line;
        while (!(line = bufferedReader.readLine()).equals(EMPTY_LINE)) {
            String[] headerValue = line.split(HEADER_REGEX);
            requestHeader.put(headerValue[HEADER_KEY], headerValue[HEADER_VALUE]);
        }
        return requestHeader;
    }

    private static Body extractBody(BufferedReader bufferedReader,
                                    Map<String, String> header) throws IOException {
        if (!header.containsKey("Content-Length")) {
            return new Body("");
        }
        int contentLength = Integer.parseInt(header.get("Content-Length").trim());
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return new Body(requestBody);
    }

    public String path() {
        return requestLine.path();
    }

    public QueryString queryString() {
        return requestLine.queryString();
    }

    public HttpMethod method() {
        return requestLine.method();
    }

    public Body body() {
        return body;
    }

    public boolean hasCookie() {
        return header.hasCookie();
    }

    public String getCookieValue(String key) {
        return header.getCookieValue(key);
    }
}

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
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeader header, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.header = header;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        Map<String, String> header = extractHeader(bufferedReader);
        RequestBody requestBody = extractBody(bufferedReader, header);
        return new HttpRequest(requestLine, RequestHeader.from(header), requestBody);
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

    private static RequestBody extractBody(BufferedReader bufferedReader,
                                           Map<String, String> header) throws IOException {
        if (!header.containsKey("Content-Length")) {
            return RequestBody.from("");
        }
        int contentLength = Integer.parseInt(header.get("Content-Length").strip());
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return RequestBody.from(requestBody);
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

    public RequestBody body() {
        return requestBody;
    }

    public boolean hasCookie() {
        return header.hasCookie();
    }

    public String getCookieValue(String key) {
        return header.getCookieValue(key);
    }

    public boolean isMethod(HttpMethod method) {
        return requestLine.isMethod(method);
    }
}

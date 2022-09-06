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
    private final Map<String, String> header;

    private HttpRequest(RequestLine requestLine, Map<String, String> header) {
        this.requestLine = requestLine;
        this.header = header;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        Map<String, String> requestHeader = new HashMap<>();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.equals(EMPTY_LINE)) {
                break;
            }
            String[] headerValue = line.split(HEADER_REGEX);
            requestHeader.put(headerValue[HEADER_KEY], headerValue[HEADER_VALUE]);
        }
        return new HttpRequest(requestLine, requestHeader);
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
}

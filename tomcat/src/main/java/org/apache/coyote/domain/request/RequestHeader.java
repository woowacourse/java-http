package org.apache.coyote.domain.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {

    private static final String HEADER_REGEX = ": ";
    private static final String EMPTY_HEADER = "";
    private static final int HEADER_KEY = 0;
    private static final int HEADER_VALUE = 1;
    private static final String CONTENT_LENGTH_KEY = "Content-Length";
    private static final String DEFAULT_CONTENT_LENGTH = "0";
    private static final String COOKIE_KEY = "Cookie";
    private static final String DEFAULT_COOKIE = "";

    private final Map<String, String> requestHeader;

    private RequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public static RequestHeader from(BufferedReader inputReader) throws IOException {
        Map<String, String> requestHeader = new HashMap<>();
        while (inputReader.ready()) {
            String line = inputReader.readLine();
            if (line.equals(EMPTY_HEADER)) {
                break;
            }
            String[] header = line.split(HEADER_REGEX);
            requestHeader.put(header[HEADER_KEY], header[HEADER_VALUE]);
        }
        return new RequestHeader(requestHeader);
    }

    public int getContentLength() {
        return Integer.parseInt(requestHeader.getOrDefault(CONTENT_LENGTH_KEY, DEFAULT_CONTENT_LENGTH));
    }

    public String getCookies() {
        return requestHeader.getOrDefault(COOKIE_KEY, DEFAULT_COOKIE);
    }
}

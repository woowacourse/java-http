package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final String HTTP_HEADER_REGEX = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final StartLine startLine;
    private final Map<String, String> headers;

    private HttpRequest(StartLine startLine, List<String> headerLines) {
        this.startLine = startLine;
        this.headers = parsingHeader(headerLines);
    }

    public static HttpRequest of(String firstLine, List<String> headerLines) {
        StartLine startLine = StartLine.from(firstLine);
        startLine.changeRequestURL();
        return new HttpRequest(startLine, headerLines);
    }

    public Map<String, String> parsingHeader(List<String> headerLines) {
        Map<String, String> headers = new HashMap<>();
        for (String headerLine : headerLines) {
            String[] item = headerLine.split(HTTP_HEADER_REGEX);
            String key = item[HEADER_KEY_INDEX];
            String value = item[HEADER_VALUE_INDEX];
            headers.put(key, value);
        }
        return headers;
    }

    public boolean isMainRequest() {
        return startLine.isMainRequest();
    }

    public boolean isLoginRequest() {
        return startLine.isLoginRequest();
    }

    public RequestURL getRequestURL() {
        return startLine.getRequestURL();
    }
}

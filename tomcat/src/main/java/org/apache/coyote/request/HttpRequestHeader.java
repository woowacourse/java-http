package org.apache.coyote.request;

import org.apache.coyote.common.HttpCookie;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.coyote.common.Headers.*;

public class HttpRequestHeader {

    private static final String HEADER_SEPERATOR = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers;

    private HttpRequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeader from(String requestHeader) {
        Map<String, String> headers = new LinkedHashMap<>();
        String[] splitedLines = requestHeader.split(CRLF);

        List<String> splited = Arrays.asList(splitedLines);
        for (String line : splited) {
            String[] parts = line.split(HEADER_SEPERATOR);
            if (parts.length == 2) {
                String key = parts[KEY_INDEX].trim();
                String value = parts[VALUE_INDEX].trim();
                headers.put(key, value);
            }
        }
        return new HttpRequestHeader(headers);
    }

    public int getContentLength() {
        String contentLength = headers.get(CONTENT_LENGTH);
        try {
            return Integer.parseInt(contentLength);
        } catch (NumberFormatException e) {
            return KEY_INDEX;
        }
    }

    public HttpCookie getCookie() {
        String cookie = headers.get(COOKIE);
        return HttpCookie.of(cookie);
    }
}

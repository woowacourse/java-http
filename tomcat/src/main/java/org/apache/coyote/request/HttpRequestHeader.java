package org.apache.coyote.request;

import org.apache.coyote.common.HttpCookie;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeader {

    private static final String SEPERATOR = "\r\n";
    private static final String HEADER_SEPERATOR = ": ";

    private final Map<String, String> headers;

    private HttpRequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeader from(String requestHeader) {
        Map<String, String> headers = new LinkedHashMap<>();
        String[] splitedLines = requestHeader.split(SEPERATOR);

        List<String> splited = Arrays.asList(splitedLines);
        for (String line : splited) {
            String[] parts = line.split(HEADER_SEPERATOR);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                headers.put(key, value);
            }
        }
        return new HttpRequestHeader(headers);
    }

    public int getContentLength() {
        String contentLength = headers.get("Content-Length");
        try {
            return Integer.parseInt(contentLength);
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    public HttpCookie getCookie() {
        String cookie = headers.get("Cookie");
        return HttpCookie.of(cookie);
    }
}

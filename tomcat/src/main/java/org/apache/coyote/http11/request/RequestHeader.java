package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.Cookie;

public class RequestHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final int HEADER_TYPE = 0;
    private static final int HEADER_VALUE = 1;

    private final Map<String, String> headers;
    private final Cookie cookie;

    public RequestHeader(Map<String, String> headers, Cookie cookie) {
        this.headers = headers;
        this.cookie = cookie;
    }

    public static RequestHeader parse(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String header = bufferedReader.readLine();
        while (!header.isBlank()) {
            String[] splitHeader = header.split(HEADER_DELIMITER);
            headers.put(splitHeader[HEADER_TYPE], splitHeader[HEADER_VALUE]);
            header = bufferedReader.readLine();
        }
        Cookie cookie = Cookie.parse(headers.getOrDefault("Cookie", ""));

        return new RequestHeader(headers, cookie);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }
}

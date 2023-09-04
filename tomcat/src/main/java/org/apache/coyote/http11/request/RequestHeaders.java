package org.apache.coyote.http11.request;

import org.apache.coyote.http11.cookie.HttpCookies;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders parse(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();

        while (isNotEmpty(line)) {
            String[] splitedLine = line.split(":", 2);

            String key = splitedLine[KEY_INDEX].toLowerCase();
            String value = splitedLine[VALUE_INDEX].trim().toLowerCase();

            headers.put(key, value);
            line = bufferedReader.readLine();
        }
        return new RequestHeaders(headers);
    }

    private static boolean isNotEmpty(String line) {
        return !"".equals(line);
    }

    public boolean hasNotBody() {
        return !headers.containsKey("content-length");
    }

    public String getHeader(String key) {
        return headers.get(key.toLowerCase());
    }

    public HttpCookies getCookies() {
        return HttpCookies.parse(headers.get("cookie"));
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
                "headers=" + headers +
                '}';
    }
}

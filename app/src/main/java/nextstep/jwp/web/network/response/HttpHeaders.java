package nextstep.jwp.web.network.response;

import nextstep.jwp.web.exception.InputException;
import nextstep.jwp.web.network.request.Cookies;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String COOKIES = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String DEFAULT_HEADER_VALUE = null;

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders of(BufferedReader bufferedReader) {
        try {
            final Map<String, String> headers = new HashMap<>();
            String line = bufferedReader.readLine();
            while (!"".equals(line)) {
                final String[] keyAndValue = line.split(":");
                headers.put(keyAndValue[KEY_INDEX].trim(), keyAndValue[VALUE_INDEX].trim());
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
            }
            return new HttpHeaders(headers);
        } catch (IOException exception) {
            throw new InputException("Exception while reading headers from http request.");
        }
    }

    public String get(String key) {
        return headers.getOrDefault(key, DEFAULT_HEADER_VALUE);
    }

    public Cookies getCookies() {
        return Cookies.of(headers.getOrDefault(COOKIES, DEFAULT_HEADER_VALUE));
    }

    public int getContentLength() {
        final String contentLengthAsString = this.headers.get(CONTENT_LENGTH);
        if (doesNotExist(contentLengthAsString)) {
            return 0;
        }
        return Integer.parseInt(contentLengthAsString);
    }

    private boolean doesNotExist(String contentLengthAsString) {
        return contentLengthAsString == null;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }
}

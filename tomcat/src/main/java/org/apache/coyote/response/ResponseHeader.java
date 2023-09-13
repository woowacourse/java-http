package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.HttpCookie;

public class ResponseHeader {

    private static final String LINE_SPLIT_DELIMITER = "\r\n";
    private static final String KEY_SPLIT_DELIMITER = ": ";

    private Map<String, String> headers;
    private HttpCookie httpCookie;

    public ResponseHeader() {
        this.headers = new HashMap<>();
        this.httpCookie = HttpCookie.from("");
    }

    public void addHeaders(String key, String value) {
        this.headers.put(key, value);
    }

    public void addCookie(String key, String value) {
        httpCookie.addCookie(key, value);
    }

    public String getHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            stringBuilder.append(key + KEY_SPLIT_DELIMITER + value + LINE_SPLIT_DELIMITER);
        }
        getCookieHeader(stringBuilder);
        return stringBuilder.toString();
    }

    private void getCookieHeader(StringBuilder stringBuilder) {
        if (httpCookie.isEmpty()) {
            return;
        }
        stringBuilder.append("Set-Cookie: " + httpCookie.getCookies());
    }

}

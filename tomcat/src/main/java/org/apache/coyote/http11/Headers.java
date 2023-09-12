package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http11.request.HttpCookie;

public class Headers {

    private static final String HEADER_DELIMITER = ":";
    private final Map<String, String> values;
    private final HttpCookie cookie;

    private Headers(Map<String, String> values, HttpCookie cookie) {
        this.values = values;
        this.cookie = cookie;
    }

    public static Headers from(final BufferedReader bufferedReader) throws IOException {
        String line = "";
        final HashMap<String, String> map = new HashMap<>();
        HttpCookie httpCookie = null;

        while (!(line = bufferedReader.readLine()).isBlank()) {
            final String[] headerInfo = line.split(HEADER_DELIMITER);
            final String headerName = headerInfo[0];
            final String value = headerInfo[1].trim();

            if (headerName.equals("Cookie")) {
                httpCookie = new HttpCookie(value);
            }
            map.put(headerName, value);
        }

        return new Headers(map, httpCookie);
    }

    public boolean containsHeader(final String header) {
        return values.containsKey(header);
    }

    public String get(final String header) {
        return values.get(header);
    }

    public boolean hasCookie() {
        return Objects.nonNull(cookie);
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}

package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.apache.coyote.util.StringParser;

public class HttpRequestBody {

    private final Map<String, String> values;

    private HttpRequestBody(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpRequestBody of(final BufferedReader bufferedReader, final int contentLength)
            throws IOException {
        if (contentLength == 0) {
            return new HttpRequestBody(Collections.emptyMap());
        }

        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);

        return new HttpRequestBody(StringParser.toMap(requestBody));
    }

    public String get(final String key) {
        return values.get(key);
    }
}

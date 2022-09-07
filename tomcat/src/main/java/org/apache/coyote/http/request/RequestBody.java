package org.apache.coyote.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.apache.coyote.util.StringParser;

public class RequestBody {

    private static final String FORM_FIELD_DELIMITER = "&";
    private static final String FORM_KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> values;

    private RequestBody(final Map<String, String> values) {
        this.values = values;
    }

    public static RequestBody of(final BufferedReader bufferedReader, final int contentLength)
            throws IOException {
        if (contentLength == 0) {
            return new RequestBody(Collections.emptyMap());
        }

        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);

        final Map<String, String> values = StringParser.split(requestBody, FORM_FIELD_DELIMITER,
                FORM_KEY_VALUE_DELIMITER);
        return new RequestBody(values);
    }

    public String get(final String key) {
        return values.get(key);
    }
}

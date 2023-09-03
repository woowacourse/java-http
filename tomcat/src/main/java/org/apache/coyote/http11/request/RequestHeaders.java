package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {

    private static final String KEY_VALUE_DELIMITER = ": ";
    private static final String SPACE_DELIMITER = " ";
    private final Map<String, String> headers;

    public RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            final String[] splitedLine = line.replace(KEY_VALUE_DELIMITER, SPACE_DELIMITER).split(SPACE_DELIMITER);
            headers.put(splitedLine[0], splitedLine[1]);
        }

        return new RequestHeaders(headers);
    }

    public String getValue(String key) {
        return headers.get(key);
    }
}

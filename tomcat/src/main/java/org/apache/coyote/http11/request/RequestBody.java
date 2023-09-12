package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String REQUEST_BODY_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> values;

    private RequestBody(Map<String, String> values) {
        this.values = values;
    }

    public static RequestBody of(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        final Map<String, String> map = new HashMap<>();

        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBodyBuffer = new String(buffer);

        final String[] requestBodies = requestBodyBuffer.split(REQUEST_BODY_DELIMITER);
        for (String body : requestBodies) {
            final String[] requestBodyInfo = body.split(KEY_VALUE_DELIMITER);
            final String requestBodyName = requestBodyInfo[0];
            final String requestBodyValue = requestBodyInfo[1];
            map.put(requestBodyName, requestBodyValue);
        }

        return new RequestBody(map);
    }

    public static RequestBody empty() {
        return new RequestBody(new HashMap<>());
    }

    public int size() {
        return values.size();
    }

    public String get(final String key) {
        return values.get(key);
    }
}

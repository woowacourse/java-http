package org.apache.coyote.domain.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String BODY_REGEX = "&";
    private static final String BODY_DELIMITER = "=";
    private static final int BODY_KEY = 0;
    private static final int BODY_VALUE = 1;

    private final Map<String, String> bodies;

    public RequestBody(Map<String, String> bodies) {
        this.bodies = bodies;
    }

    public static RequestBody of(BufferedReader inputReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        inputReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return new RequestBody(generateBody(requestBody));
    }

    private static Map<String, String> generateBody(String body) {
        Map<String, String> requestBody = new HashMap<>();
        if (body.length() == 0) {
            return requestBody;
        }
        String[] bodies = body.split(BODY_REGEX);
        for (String data : bodies) {
            String[] keyAndValue = data.split(BODY_DELIMITER);
            requestBody.put(keyAndValue[BODY_KEY], keyAndValue[BODY_VALUE]);
        }
        return requestBody;
    }

    public Map<String, String> getBodies() {
        return bodies;
    }
}

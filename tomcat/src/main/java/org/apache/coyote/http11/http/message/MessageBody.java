package org.apache.coyote.http11.http.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessageBody {

    private static final String BODY_VALUES_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> messageBody;

    private MessageBody(final Map<String, String> messageBody) {
        this.messageBody = messageBody;
    }

    public static MessageBody from(final BufferedReader bufferedReader, final RequestHeaders requestHeaders) throws IOException {
        if (requestHeaders.containsKey(CONTENT_LENGTH)) {
            return new MessageBody(parseHttpRequestBody(bufferedReader, Integer.parseInt(requestHeaders.getValue(CONTENT_LENGTH))));
        }
        return MessageBody.ofEmpty();
    }

    private static MessageBody ofEmpty() {
        return new MessageBody(new HashMap<>());
    }

    private static Map<String, String> parseHttpRequestBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String body = new String(buffer);
        return splitBody(body);
    }

    private static Map<String, String> splitBody(final String body) {
        final Map<String, String> requestBody = new HashMap<>();
        final String[] splitBodies = body.split(BODY_VALUES_DELIMITER);
        for (final String splitBody : splitBodies) {
            final String[] keyAndValue = splitBody.split(KEY_VALUE_DELIMITER);
            requestBody.put(keyAndValue[0], keyAndValue[1]);
        }

        return requestBody;
    }

    public Map<String, String> getBody() {
        return Collections.unmodifiableMap(messageBody);
    }
}

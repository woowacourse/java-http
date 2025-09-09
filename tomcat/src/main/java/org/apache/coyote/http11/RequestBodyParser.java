package org.apache.coyote.http11;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class RequestBodyParser {

    private static final ObjectReader OBJECT_READER = new ObjectMapper().reader();

    public static <T> T parse(String body, Class<T> type) {
        try {
            return OBJECT_READER.readValue(body, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse request body", e);
        }
    }
}

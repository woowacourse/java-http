package org.apache.coyote.http11.request.parser;

import java.util.Map;

public class PlainTextParser implements RequestBodyParser {

    private static final String PLAIN_TEXT_KEY = "text";

    @Override
    public Map<String, String> parseParameters(String requestBody) {
        return Map.of(PLAIN_TEXT_KEY, requestBody);
    }
}

package org.apache.coyote.http11.request.parser;

import java.util.Map;

public class PlainTextParser implements RequestBodyParser {

    private static final String REQUEST_BODY_KEY = "text";

    @Override
    public Map<String, String> parseParameters(String requestBody) {
        return Map.of(REQUEST_BODY_KEY, requestBody);
    }
}

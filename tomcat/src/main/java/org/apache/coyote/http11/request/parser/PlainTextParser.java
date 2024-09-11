package org.apache.coyote.http11.request.parser;

import java.util.Map;

public class PlainTextParser implements RequestBodyParser {

    @Override
    public Map<String, String> parseParameters(String requestBody) {
        return Map.of(requestBody, requestBody);
    }
}

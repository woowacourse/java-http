package org.apache.coyote.common.request.parser.bodyparser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlainTextBodyParser implements Function<String, Map<String, String>> {

    PlainTextBodyParser() {}

    @Override
    public Map<String, String> apply(final String plainText) {
        return new HashMap<>(Map.of(plainText, plainText));
    }
}

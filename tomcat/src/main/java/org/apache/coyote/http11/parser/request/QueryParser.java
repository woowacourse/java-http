package org.apache.coyote.http11.parser.request;

import static org.apache.coyote.http11.HttpConstants.AMPERSAND;
import static org.apache.coyote.http11.HttpConstants.EMPTY;
import static org.apache.coyote.http11.HttpConstants.EQUAL;

import java.util.LinkedHashMap;
import java.util.Map;

public final class QueryParser {

    private QueryParser() {
    }

    public static Map<String, String> parse(final String query) {
        final Map<String, String> queryParams = new LinkedHashMap<>();
        final String[] pairs = query.split(AMPERSAND);

        for (final String pair : pairs) {
            final String[] keyValue = pair.split(EQUAL, 2);
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                queryParams.put(keyValue[0], EMPTY);
            }
        }

        return queryParams;
    }
}

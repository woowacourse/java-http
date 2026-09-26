package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public final class FormUrlEncoded {

    private FormUrlEncoded() {
    }

    public static Map<String, String> parse(final String queryString) {
        final String[] queries = queryString.split("&");
        final Map<String, String> params = new HashMap<>();
        for (final String query : queries) {
            final String[] pair = query.split("=", 2);
            if (pair.length == 2) {
                final String name = URLDecoder.decode(pair[0].strip(), UTF_8);
                final String value = URLDecoder.decode(pair[1].strip(), UTF_8);
                params.put(name, value);
            }
        }
        return Map.copyOf(params);
    }
}

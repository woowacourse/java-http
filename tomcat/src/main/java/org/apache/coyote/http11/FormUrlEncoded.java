package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FormUrlEncoded {

    private FormUrlEncoded() {
    }

    public static Map<String, List<String>> parse(final String queryString) {
        final String[] queries = queryString.split("&");
        final Map<String, List<String>> params = new HashMap<>();
        for (final String query : queries) {
            final String[] pair = query.split("=", 2);
            if (pair.length == 2) {
                final String name = URLDecoder.decode(pair[0].strip(), UTF_8);
                final String value = URLDecoder.decode(pair[1].strip(), UTF_8);
                params.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
            }
        }
        params.replaceAll((name, values) -> List.copyOf(values));
        return Map.copyOf(params);
    }
}

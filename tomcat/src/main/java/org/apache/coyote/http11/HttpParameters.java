package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class HttpParameters {

    private final Map<String, List<String>> values;

    private HttpParameters(Map<String, List<String>> values) {
        Map<String, List<String>> immutableValues = new HashMap<>();
        values.forEach((name, parameterValues) ->
                immutableValues.put(name, List.copyOf(parameterValues))
        );
        this.values = Map.copyOf(immutableValues);
    }

    static HttpParameters empty() {
        return new HttpParameters(Map.of());
    }

    static HttpParameters parse(String value) {
        if (value == null || value.isEmpty()) {
            return empty();
        }

        Map<String, List<String>> parameters = new HashMap<>();
        for (String parameter : value.split("&")) {
            String[] pair = parameter.split("=", 2);
            if (pair.length != 2) {
                continue;
            }

            parameters.computeIfAbsent(decode(pair[0]), ignored -> new ArrayList<>())
                    .add(decode(pair[1]));
        }
        return new HttpParameters(parameters);
    }

    String getFirst(String name) {
        List<String> parameterValues = getAll(name);
        if (parameterValues.isEmpty()) {
            return null;
        }
        return parameterValues.getFirst();
    }

    List<String> getAll(String name) {
        return values.getOrDefault(name, List.of());
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new HttpRequestParseException("잘못된 URL 인코딩입니다.", e);
        }
    }
}

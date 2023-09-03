package org.apache.coyote.http11.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryStringParser {

    private static final Pattern QUERY_STRING_PATTERN = Pattern.compile("([^&=]+)=([^&]*)");

    private QueryStringParser() {
    }

    public static Map<String, List<String>> parse(String query) {
        HashMap<String, List<String>> result = new HashMap<>();
        Matcher matcher = QUERY_STRING_PATTERN.matcher(query);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            List<String> values = result.getOrDefault(key, new ArrayList<>());
            values.add(value);

            result.put(key, values);
        }

        return result;
    }

    public static String parse(Map<String, List<String>> queryParams) {
        return queryParams.entrySet()
                .stream()
                .map(entry -> QueryStringParser.create(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String create(String key, List<String> values) {
        return values.stream()
                .map(value -> String.format("%s=%s", key, value))
                .collect(Collectors.joining("&"));
    }
}

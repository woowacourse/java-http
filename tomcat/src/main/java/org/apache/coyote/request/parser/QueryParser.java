package org.apache.coyote.request.parser;

import org.apache.coyote.util.MapMaker;

import java.util.List;
import java.util.Map;

public class QueryParser {

    private static final String QUERY_SEPARATOR = "&";
    private static final String PARAM_SEPARATOR = "=";

    private QueryParser() {
    }

    public static Map<String, String> parse(String query) {
        try {
            List<String> keys = List.of(query.split(QUERY_SEPARATOR));
            return keys.stream()
                    .map(key -> key.split(PARAM_SEPARATOR))
                    .collect(MapMaker.toMap(key -> key));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("파싱할 수 없는 형태의 쿼리입니다.");
        }
    }
}

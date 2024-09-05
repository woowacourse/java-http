package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class QueryStringParser {
    private static final String PARAMETER_DELIMITER = "&";
    private static final String ELEMENT_DELIMITER = "=";

    public static Map<String, String> parse(String queryString) {
        Map<String, String> parameters = new HashMap<>();
        String[] elements = queryString.split(PARAMETER_DELIMITER);

        Stream.of(elements)
                .forEach(element -> parseElement(parameters, element));

        return parameters;
    }

    private static void parseElement(Map<String, String> parameters, String element) {
        if (!element.contains(ELEMENT_DELIMITER)) {
            throw new IllegalArgumentException("잘못된 쿼리 파라미터입니다.");
        }

        String[] k = element.split("=");
        parameters.put(k[0], k[1]);
    }

}

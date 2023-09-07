package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.servlet.util.StringFormatParser;

public class QueryParameters {

    private static final String DELIMITER = "&";
    private static final String PAIR_BRIDGE = "=";

    private final Map<String, List<String>> values;

    private QueryParameters(final Map<String, List<String>> values) {
        this.values = values;
    }

    public static QueryParameters from(final String query) {
        return new QueryParameters(StringFormatParser.parseMultiValuePairs(query, DELIMITER, PAIR_BRIDGE));
    }

    public String findSingleByKey(final String key) {
        return findAllByKey(key).get(0);
    }

    public List<String> findAllByKey(final String key) {
        return values.get(key);
    }

    public Map<String, List<String>> getValues() {
        return new HashMap<>(values);
    }
}

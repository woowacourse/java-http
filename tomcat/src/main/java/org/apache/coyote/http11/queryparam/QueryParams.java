package org.apache.coyote.http11.queryparam;

import util.BiValue;
import util.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParams {
    private static final String DELIMITER = "&";
    private static final String QUERY_DELIMITER = "=";

    private final Map<String, String> params;

    public static QueryParams from(final String line) {
        return new QueryParams(Arrays.stream(line.split(DELIMITER))
                .map(s -> StringUtil.split(s, QUERY_DELIMITER))
                .filter(BiValue::secondNotNull)
                .collect(Collectors.toMap(BiValue::first, BiValue::second)));
    }

    public QueryParams(final Map<String, String> params) {
        this.params = params;
    }

    public String getQueryParam(final String name) {
        return StringUtil.blankIfNull(params.get(name));
    }
}

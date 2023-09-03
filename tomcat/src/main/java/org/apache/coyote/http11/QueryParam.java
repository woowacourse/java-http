package org.apache.coyote.http11;

public class QueryParam {

    private static final String QUERY_PARAMETER_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String key;
    private final String value;

    public QueryParam(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static QueryParam from(String queryParam) {
        final String[] splitParam = queryParam.split("=");

        return new QueryParam(splitParam[0], splitParam[1]);
    }

    public String getValue() {
        return value;
    }

    public boolean isSameKey(final String key) {
        return this.key.equals(key);
    }
}

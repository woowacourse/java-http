package org.apache.coyote.request;

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
        final String[] splitParam = queryParam.split(QUERY_PARAMETER_DELIMITER);

        return new QueryParam(splitParam[KEY_INDEX], splitParam[VALUE_INDEX]);
    }

    public String getValue() {
        return value;
    }

    public boolean isSameKey(final String key) {
        return this.key.equals(key);
    }
}

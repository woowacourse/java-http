package org.apache.coyote.request.query;

public class QueryParam {

    private static final String QUERY_PARAMETER_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int INVALID_QUERY_SIZE = 1;

    private final String key;
    private final String value;

    public QueryParam(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static QueryParam from(String queryParam) {
        final String[] splitParam = queryParam.split(QUERY_PARAMETER_DELIMITER);
        checkQueryParamSize(splitParam);

        return new QueryParam(splitParam[KEY_INDEX], splitParam[VALUE_INDEX]);
    }

    private static void checkQueryParamSize(final String[] splitParam) {
        if (splitParam.length == INVALID_QUERY_SIZE) {
            throw new IllegalArgumentException("잘못된 쿼리 형식입니다.");
        }
    }

    public String getValue() {
        return value;
    }

    public boolean isSameKey(final String key) {
        return this.key.equals(key);
    }
}

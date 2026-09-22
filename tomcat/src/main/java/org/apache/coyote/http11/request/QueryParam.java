package org.apache.coyote.http11.request;

public class QueryParam {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int LIMIT = 2;
    private static final String EMPTY_VALUE = "";

    private final String key;
    private final String value;

    public QueryParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Boolean isSameKey(String key) {
        return this.key.equals(key);
    }

    public String getValue() {
        return value;
    }

    public static QueryParam from(String s) {
        String[] param = s.split(KEY_VALUE_DELIMITER, LIMIT);
        String key = param[0];
        String value = (param.length > 1) ? param[1] : EMPTY_VALUE;
        return new QueryParam(key, value);
    }
}

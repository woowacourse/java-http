package org.apache.coyote.http11;

public class QueryParam {

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
        String[] param = s.split("=");
        return new QueryParam(param[0], param[1]);
    }
}

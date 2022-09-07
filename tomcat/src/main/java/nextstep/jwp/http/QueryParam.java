package nextstep.jwp.http;

public class QueryParam {

    private static final String QUERY_PARAM_SEPARATOR = "=";
    private static final int QUERY_PARAM_BEGUN_INDEX = 0;
    private static final int EXCLUDING_HERE = 1;
    private final String key;
    private final String value;

    public QueryParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static QueryParam from(String queryParam) {
        int separatorIndex = queryParam.lastIndexOf(QUERY_PARAM_SEPARATOR);
        String key = queryParam.substring(QUERY_PARAM_BEGUN_INDEX, separatorIndex);
        String value = queryParam.substring(EXCLUDING_HERE + separatorIndex);
        return new QueryParam(key, value);
    }

    public String getValue() {
        return value;
    }

    public boolean same(String key) {
        return this.key.equals(key);
    }
}

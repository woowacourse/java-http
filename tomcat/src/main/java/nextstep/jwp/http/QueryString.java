package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {

    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int KET_VALUE_SIZE = 2;
    
    private final Map<String, String> params;

    private QueryString(Map<String, String> params) {
        this.params = params;
    }

    public static QueryString from(String line) {
        if (line == null) {
            throw new IllegalArgumentException("line is Null");
        }

        String[] params = line.split(QUERY_STRING_DELIMITER);

        Map<String, String> paramMap = Arrays.stream(params)
                .map(param -> param.split(KEY_VALUE_DELIMITER))
                .filter(param -> param.length == KET_VALUE_SIZE)
                .collect(Collectors.toMap(param -> param[KEY_INDEX], param -> param[VALUE_INDEX]));

        return new QueryString(paramMap);
    }

    public boolean hasValues() {
        return params.size() != 0;
    }

    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("QueryString key is Null");
        }

        return params.get(key);
    }

}

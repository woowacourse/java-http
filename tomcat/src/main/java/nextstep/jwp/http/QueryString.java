package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {

    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final Map<String, String> params;

    private QueryString(Map<String, String> params) {
        this.params = params;
    }

    public static QueryString from(String line) {
        if ("".equals(line)) {
            return new QueryString(Collections.emptyMap());
        }

        String[] params = line.split(QUERY_STRING_DELIMITER);

        Map<String, String> paramMap = Arrays.stream(params)
                .map(param -> param.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(param -> param[KEY_INDEX], param -> param[VALUE_INDEX]));

        return new QueryString(paramMap);
    }

    public boolean hasValues() {
        return params.size() != 0;
    }

    public String get(String key) {
        if (params.containsKey(key)) {
            return params.get(key);
        }

        return null;
    }

}

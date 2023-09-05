package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {

    private final Map<String, String> params;

    private QueryString(Map<String, String> params) {
        this.params = params;
    }

    public static QueryString from(String line) {
        if ("".equals(line)) {
            return new QueryString(Collections.emptyMap());
        }

        String[] params = line.split("&");

        Map<String, String> paramMap = Arrays.stream(params)
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));

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
